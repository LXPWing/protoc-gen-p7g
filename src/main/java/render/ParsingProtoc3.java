package render;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import dto.JavaMessage;
import dto.Message;
import utils.ConvertUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParsingProtoc3 implements IProtocl<Descriptors.Descriptor>{
    // CodeGeneratorRequest contain FileDescriptorProtos for all the proto files we need to process
    // as well as their dependencies.  We want to convert the FileDescriptorProtos into FileDescriptor instances,
    // since they are easier to work with. We will build a map that maps file names to the corresponding file
    // descriptor.
    public static final Map<String, Descriptors.FileDescriptor> filesByName = new HashMap<>();

    private Message message;

    public static Map<String, Descriptors.FileDescriptor> doAnalysisProtoFile(PluginProtos.CodeGeneratorRequest request) throws Descriptors.DescriptorValidationException {
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

        return filesByName;
    }


    public static StringBuilder getFields(Map<String, Descriptors.FileDescriptor> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String, Descriptors.FileDescriptor> entry:map.entrySet()){
            Descriptors.FileDescriptor value = entry.getValue();
            for (Descriptors.EnumDescriptor enumType : value.getEnumTypes()) {
                System.out.println(enumType.getName());
                System.out.println(enumType.getValues());
            }

            for (Descriptors.Descriptor messageType : value.getMessageTypes()) {
                //generateMessage(stringBuilder, messageType, 0);
                for (Descriptors.FieldDescriptor field : messageType.getFields()) {
                    System.out.println(field.getName());
//                    System.out.println(messageType.getFields().size());
                }

            }
        }
        return stringBuilder;
    }

    private static void generateMessage(StringBuilder sb, Descriptors.Descriptor messageType, int indent) {
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        sb.append("|- ");
        sb.append(messageType.getName());
        sb.append("(");

        JavaMessage javaMessage = new JavaMessage();
        javaMessage.setClassName(messageType.getName());

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
        for (Descriptors.Descriptor nestedType : messageType.getNestedTypes()) {
            System.out.println(nestedType.getName());
            generateMessage(sb, nestedType, indent + 3);
        }
    }

    private static String renderType(Descriptors.FieldDescriptor fd) {
        if (fd.isRepeated()) {
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

    private Map<String, String> getJavaType(List<Descriptors.FieldDescriptor> fieldDescriptorList){
        Map<String, String> map = new HashMap<>();
        for(Descriptors.FieldDescriptor fields:fieldDescriptorList){
            String name = fields.getName();
            String type = String.valueOf(fields.getJavaType());
            map.put(name, type);
        }

        return map;
    }

    public ParsingProtoc3(Message message) {
        this.message = message;
    }

    @Override
    public void getCommonFields(Descriptors.Descriptor descriptor) {
        List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
        Map<String, String> map = new HashMap<>();
        for (Descriptors.FieldDescriptor field : fields) {
            String name = field.getName();
            String type = ConvertUtil.convertToString(field.getType());
            if(type.equals("Enum") || type.equals("Message")) continue;
            map.put(name, type);
        }
    }

    @Override
    public void doAnalysisProtoFile(Object request) {

    }

    @Override
    public void getMapField(Descriptors.Descriptor descriptor) {
        List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
        List<Descriptors.FieldDescriptor> message = fields.stream()
                .filter(item -> !item.isMapField())
                .collect(Collectors.toList());
        message.forEach(item -> {
            String name = item.getName();
        });
    }

    @Override
    public void getEnumField(Descriptors.Descriptor descriptor) {
        List<Descriptors.EnumDescriptor> enumTypes = descriptor.getEnumTypes();
        Map<String, List<Descriptors.EnumValueDescriptor>> map = new HashMap<>();
        enumTypes.forEach(item -> {
            String name = item.getName();
            List<Descriptors.EnumValueDescriptor> values = item.getValues();
            map.put(name, values);
        });
    }
}
