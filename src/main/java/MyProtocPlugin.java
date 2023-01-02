import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import freemarker.cache.ByteArrayTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import p7g.dto.JavaMessage;
import p7g.dto.MapKV;
import p7g.dto.Message;
import p7g.utils.ConvertUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MyProtocPlugin {
    private static Map<String, JavaMessage> map = new HashMap<>();
    private static List ignoreList = new ArrayList();
    private static List ignoreNameList = new ArrayList();

    public static void main(String[] args) throws IOException, Descriptors.DescriptorValidationException, TemplateException {
        // Plugin receives a serialized CodeGeneratorRequest via stdin
        PluginProtos.CodeGeneratorRequest request = PluginProtos.CodeGeneratorRequest.parseFrom(System.in);

        // CodeGeneratorRequest contain FileDescriptorProtos for all the proto files we need to process
        // as well as their dependencies.  We want to convert the FileDescriptorProtos into FileDescriptor instances,
        // since they are easier to work with. We will build a map that maps file names to the corresponding file
        // descriptor.
        Map<String, Descriptors.FileDescriptor> filesByName = new HashMap<>();

        for (DescriptorProtos.FileDescriptorProto fp: request.getProtoFileList()) {
            // The dependencies of fp are provided as strings, we look them up in the map as we are generating it.
            Descriptors.FileDescriptor dependencies[] =
                    fp.getDependencyList().stream().map(filesByName::get).toArray(Descriptors.FileDescriptor[]::new);

            Descriptors.FileDescriptor fd  = Descriptors.FileDescriptor.buildFrom(fp, dependencies);

            filesByName.put(
                    fp.getName(),
                    fd
            );
        }

        // Building the response
        PluginProtos.CodeGeneratorResponse.Builder response = PluginProtos.CodeGeneratorResponse.newBuilder();

        for (String fileName : request.getFileToGenerateList()) {
            // 文件递归
            Descriptors.FileDescriptor fd = filesByName.get(fileName);
            response.addFileBuilder()
                    .setName(fd.getFullName().replaceAll("\\.proto$", ".txt"))
                    .setContent(generateFileContent(fd));
        }

        // Serialize the response to stdout
        //response.build().writeTo(System.out);
    }

    private static String generateFileContent(Descriptors.FileDescriptor fd) throws IOException, TemplateException {
        StringBuilder s = new StringBuilder();
        // 文件字段递归
        for (Descriptors.Descriptor messageType : fd.getMessageTypes()) {
            JavaMessage javaMessage = new JavaMessage();
            javaMessage.setPackagePath(messageType.getFile().getPackage());
            //System.out.println(messageType.getFile().getDependencies().toString());
            generateMessage(s, messageType, 0, javaMessage);
            map.put(javaMessage.getClassName(), javaMessage);
        }

        for (Map.Entry<String, JavaMessage> stringJavaMessageEntry : map.entrySet()) {
            JavaMessage value = stringJavaMessageEntry.getValue();
            render(value);
            System.out.println(value.toString());
        }

        return map.toString();
    }


    public static void render(Message message) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setClassForTemplateLoading(MyProtocPlugin.class, "/templates/");
        cfg.setDefaultEncoding("UTF-8"); // 指定字符编码
        cfg.setLocale(Locale.CHINESE); // 指定本地语言
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template template = cfg.getTemplate("dto.ftl");
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);

        Writer consoleWriter = new OutputStreamWriter(System.out);
        template.process(map, consoleWriter);

        // 将输出结果保存到文件中
        Writer fileWriter = new FileWriter(new File("output.java"));
        try {
            template.process(map, fileWriter);
        } finally {
            fileWriter.close();
        }
    }

    private static String renderType(Descriptors.FieldDescriptor fd) {
        if (fd.isRepeated() && !fd.isMapField()) {
            return "List<" + renderSingleType(fd) + ">";
        } else {
            return renderSingleType(fd);
        }
    }

    private static String renderSingleType(Descriptors.FieldDescriptor fd) {
        if (fd.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
            return fd.getType().toString();
        } else {
            return fd.getMessageType().getName();
        }
    }

    private static JavaMessage generateMessage(StringBuilder sb, Descriptors.Descriptor messageType, int indent, JavaMessage message) {
//        message.setClassName(messageType.getName());
//        sb.append(String.join("", Collections.nCopies(indent, " ")));
//        sb.append("|- ");
//        sb.append(messageType.getName());
//        sb.append("(");
        message.setClassName(messageType.getName());
        Map<String, MapKV> mapField = getMapField(messageType);
        message.setMapInfo(mapField);
        Map<String, List<Descriptors.EnumValueDescriptor>> enumField = getEnumField(messageType);
        message.setEnumInfo(enumField);
        Map<String, String> commonFields = getCommonFields(messageType);
        message.setMessageTypeInfo(commonFields);


//        sb.append(
//            String.join(
//                ", ",
//                messageType
//                    .getFields()
//                    .stream()
//                    .map(field -> field.getName() + ": " + renderType(field))
//                    .collect(Collectors.joining(", "))
//            )
//        );
//        sb.append(")");
//        sb.append(System.getProperty("line.separator"));

        // recurse for nested messages.
        //sb.append(String.join("", Collections.nCopies(indent, " ")));
        //sb.append("\n");
        JavaMessage javaMessage = new JavaMessage();
        Map<String, Message> messageMap = new HashMap<>();
        for (Descriptors.Descriptor nestedType : messageType.getNestedTypes()) {
            if(ignoreList.contains(nestedType))continue;
            generateMessage(sb, nestedType, indent + 3, javaMessage);
            messageMap.put(javaMessage.getClassName(), javaMessage);
        }
        message.setEmbedMessageInfo(messageMap);
        return javaMessage;
    }


    // =================
    public static Map<String, MapKV> getMapField(Descriptors.Descriptor descriptor) {
        List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
        Map<String, MapKV> map = new HashMap<>();
        List<String> names = new ArrayList<>();
        fields.forEach(item -> {
            if(item.isMapField()){
                String name = item.getName();
                char[] cs=name.toCharArray();
                cs[0]-=32;
                StringBuilder sb = new StringBuilder(String.valueOf(cs));
                sb.append("Entry");
                ignoreNameList.add(sb.toString());
                names.add(sb.toString());
            }
        });

        for (Descriptors.Descriptor nestedType : descriptor.getNestedTypes()) {
            if(names.contains(nestedType.getName())){
                ignoreList.add(nestedType);
                String collect1 = nestedType.getFields()
                        .stream()
                        .map(field -> field.getName() + ": " + renderType(field))
                        .collect(Collectors.joining(", "));
                // className还原
                String oldName = nestedType.getName();
                char[] cs = oldName.toCharArray();
                cs[0] += 32;
                int len = cs.length;
                StringBuilder sb = new StringBuilder(String.valueOf(cs));
                String newName = sb.substring(0, len - 5);
                MapKV mapKV = new MapKV();
                List<Descriptors.FieldDescriptor> fields1 = nestedType.getFields();
                mapKV.setKey(renderType(fields1.get(0)));
                mapKV.setValue(renderType(fields1.get(1)));
                map.put(newName, mapKV);
            }
        }

        return map;
    }


    public static Map<String, List<Descriptors.EnumValueDescriptor>> getEnumField(Descriptors.Descriptor descriptor) {
        List<Descriptors.EnumDescriptor> enumTypes = descriptor.getEnumTypes();
        Map<String, List<Descriptors.EnumValueDescriptor>> map = new HashMap<>();
        enumTypes.forEach(item -> {
            String name = item.getName();
            List<Descriptors.EnumValueDescriptor> values = item.getValues();
            map.put(name, values);
        });

        return map;
    }

    public static Map<String, String> getCommonFields(Descriptors.Descriptor descriptor) {
        List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
        Map<String, String> map = new HashMap<>();
        for (Descriptors.FieldDescriptor field : fields) {
            String name = field.getName();
            String type = ConvertUtil.convertToString(field.getType());
            if(type.equals("Enum") || field.isMapField()) continue;
            map.put(name, renderType(field));
        }

        return map;
    }

    public static Map<String, String> getListFields(Descriptors.Descriptor descriptor) {
        // TODO 解析list字段

        return null;
    }
}