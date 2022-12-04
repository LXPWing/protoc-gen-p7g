import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import render.ParsingProtoc3;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, Descriptors.DescriptorValidationException {
        // Plugin receives a serialized CodeGeneratorRequest via stdin
        PluginProtos.CodeGeneratorRequest request = PluginProtos.CodeGeneratorRequest.parseFrom(System.in);

        StringBuilder fields = ParsingProtoc3.getFields(ParsingProtoc3.doAnalysisProtoFile(request));

        // Building the response
        PluginProtos.CodeGeneratorResponse.Builder response = PluginProtos.CodeGeneratorResponse.newBuilder();
        response.addFileBuilder()
                .setName("greeter".replaceAll("\\.proto$", ".txt"))
                .setContent(fields.toString());
        // Serialize the response to stdout
        //response.build().writeTo(System.out);
    }

    private static String generateFileContent(Descriptors.FileDescriptor fd) {
        StringBuilder sb = new StringBuilder();
        for (Descriptors.Descriptor messageType : fd.getMessageTypes()) {
            generateMessage(sb, messageType, 0);
        }
        return sb.toString();
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

    private static void generateMessage(StringBuilder sb, Descriptors.Descriptor messageType, int indent) {
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        sb.append("|- ");
        sb.append(messageType.getName());
        sb.append("(");

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
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        for (Descriptors.Descriptor nestedType : messageType.getNestedTypes()) {
            generateMessage(sb, nestedType, indent + 3);
        }
    }
}
