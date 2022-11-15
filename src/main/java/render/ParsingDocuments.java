package render;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ParsingDocuments {
    // CodeGeneratorRequest contain FileDescriptorProtos for all the proto files we need to process
    // as well as their dependencies.  We want to convert the FileDescriptorProtos into FileDescriptor instances,
    // since they are easier to work with. We will build a map that maps file names to the corresponding file
    // descriptor.
    Map<String, Descriptors.FileDescriptor> filesByName = new HashMap<>();

    public void doAnalysis(PluginProtos.CodeGeneratorRequest request) throws Descriptors.DescriptorValidationException {
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
    }

    public static String generateFileContent(Descriptors.FileDescriptor fd) {
        StringBuilder sb = new StringBuilder();
        for (Descriptors.Descriptor messageType : fd.getMessageTypes()) {
            generateMessage(sb, messageType, 0);
        }
        return sb.toString();
    }

    private static void generateMessage(StringBuilder sb, Descriptors.Descriptor messageType, int indent) {
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        sb.append("|- ");
        sb.append(messageType.getName());
        sb.append("(");

//        sb.append(
//                String.join(
//                        ", ",
//                        messageType
//                                .getFields()
//                                .stream()
//                                .map(field -> field.getName() + ": " + renderType(field))
//                                .collect(Collectors.joining(", "))
//                )
//        );
//        sb.append(")");
//        sb.append(System.getProperty("line.separator"));
//
//        // recurse for nested messages.
//        sb.append(String.join("", Collections.nCopies(indent, " ")));
//        for (Descriptors.Descriptor nestedType : messageType.getNestedTypes()) {
//            generateMessage(sb, nestedType, indent + 3);
//        }
    }
}
