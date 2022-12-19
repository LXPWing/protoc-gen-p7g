import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import dto.JavaMessage;
import utils.ConvertUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyProtocPlugin {
    private Map<String, JavaMessage> map = new HashMap<>();
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
        }
        return s.toString();
    }

    private static String renderType(Descriptors.FieldDescriptor fd) {
        if (fd.isRepeated() && !fd.isMapField()) {
            return "List<" + renderSingleType(fd) + ">";
        }

        //System.out.println(fd.getType());

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

    private static StringBuilder generateMessage(StringBuilder sb, Descriptors.Descriptor messageType, int indent, JavaMessage message) {
        message.setClassName(messageType.getName());
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        sb.append("|- ");
        sb.append(messageType.getName());
        sb.append("(");

//        List<Descriptors.EnumDescriptor> enumTypes = messageType.getEnumTypes();
//        enumTypes.forEach(item -> {
//            String name = item.getName();
//            String value = String.valueOf(item.getValues());
//            sb.append(name + ":" + value);
//        });
        Map<String, List<Descriptors.EnumValueDescriptor>> enumField = getEnumField(messageType);
        message.setEnumInfo(enumField);
        Map<String, String> commonFields = getCommonFields(messageType);
        message.setMessageTypeInfo(commonFields);
        getMapField(messageType);

        sb.append(
            String.join(
                ", ", 
                messageType
                    .getFields()
                    .stream()
                    .map(field -> field.getName() + ": " + renderType(field))
                    .collect(Collectors.joining(", "))
            )
        );
        sb.append(")");
        sb.append(System.getProperty("line.separator"));

        // recurse for nested messages.
        //sb.append(String.join("", Collections.nCopies(indent, " ")));
        sb.append("\n");
        for (Descriptors.Descriptor nestedType : messageType.getNestedTypes()) {
            JavaMessage javaMessage = new JavaMessage();
            generateMessage(sb, nestedType, indent + 3, javaMessage);
        }
        return sb;
    }


    // =================
    public static void getMapField(Descriptors.Descriptor descriptor) {
        List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
        List<Descriptors.FieldDescriptor> message = fields.stream()
                .filter(item -> !item.isMapField())
                .collect(Collectors.toList());
        for (Descriptors.Descriptor nestedType : descriptor.getNestedTypes()) {
            String collect = nestedType
                    .getFields()
                    .stream()
                    .map(field -> field.getName() + ": " + renderType(field))
                    .collect(Collectors.joining(", "));
            System.out.println(collect);
        }
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