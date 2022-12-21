import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import dto.JavaMessage;
import dto.MapKV;
import dto.Message;
import utils.ConvertUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MyProtocPlugin {
    private static Map<String, JavaMessage> map = new HashMap<>();
    private static JavaMessage javaMessage = new JavaMessage();
    public static void main(String[] args) throws IOException, Descriptors.DescriptorValidationException {
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
            Descriptors.FileDescriptor fd = filesByName.get(fileName);

            javaMessage.setClassName(fileName);
            javaMessage.setMessageTypeInfo(new HashMap<>());
            //javaMessage.set

            response.addFileBuilder()
                    .setName(fd.getFullName().replaceAll("\\.proto$", ".txt"))
                    .setContent(generateFileContent(fd));
        }

        // Serialize the response to stdout
        //response.build().writeTo(System.out);
    }

    private static String generateFileContent(Descriptors.FileDescriptor fd) {
        StringBuilder s = new StringBuilder();
        // 获取字段
        for (Descriptors.Descriptor messageType : fd.getMessageTypes()) {
            JavaMessage javaMessage = new JavaMessage();
            generateMessage(s, messageType, 0, javaMessage);
            map.put(javaMessage.getClassName(), javaMessage);
        }
        return map.toString();
    }

    private static String renderType(Descriptors.FieldDescriptor fd) {
        if (fd.isRepeated() && !fd.isMapField()) {
            return "List<" + renderSingleType(fd) + ">";
        }

        if(fd.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            Descriptors.EnumDescriptor enumType = fd.getEnumType();
            System.out.println(enumType.getValues());
            return enumType.getValues().toString();
        } else {
            return renderSingleType(fd);
        }
    }

    private static String renderSingleType(Descriptors.FieldDescriptor fd) {
        if (fd.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
            return fd.getType().toString();
        } else {
            //if(fd.isMapField())return "";
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
        Map<String, List<Descriptors.EnumValueDescriptor>> enumField = getEnumField(messageType);
        message.setEnumInfo(enumField);
        Map<String, String> commonFields = getCommonFields(messageType);
        message.setMessageTypeInfo(commonFields);
        Map<String, MapKV> mapField = getMapField(messageType);
        message.setMapInfo(mapField);

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
            JavaMessage javaMessage1 = generateMessage(sb, nestedType, indent + 3, javaMessage);
            messageMap.put(javaMessage1.getClassName(), javaMessage1);
            //message.setEmbedMessageInfo();
        }
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
                names.add(sb.toString());
            }
        });

        for (Descriptors.Descriptor nestedType : descriptor.getNestedTypes()) {
            if(names.contains(nestedType.getName())){
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

                //System.out.println(nestedType.getName() + ":" + collect1);
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
            if(type.equals("Enum") || type.equals("Message")) continue;
            map.put(name, type);
        }

        return map;
    }
}