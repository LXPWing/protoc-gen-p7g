import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;

import java.io.IOException;

public class PluginResponse {
    private PluginProtos.CodeGeneratorRequest request;

    public PluginResponse(PluginProtos.CodeGeneratorRequest request) {
        this.request = request;
    }

    public void build(PluginProtos.CodeGeneratorRequest request) throws IOException {
        // Building the response
        PluginProtos.CodeGeneratorResponse.Builder response = PluginProtos.CodeGeneratorResponse.newBuilder();

//        for (String fileName : request.getFileToGenerateList()) {
//            Descriptors.FileDescriptor fd = filesByName.get(fileName);
//            response.addFileBuilder()
//                    .setName(fd.getFullName().replaceAll("\\.proto$", ".txt"))
//                    .setContent(generateFileContent(fd));
//        }

        // Serialize the response to stdout
        response.build().writeTo(System.out);
    }


}
