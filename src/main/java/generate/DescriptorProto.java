package generate;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;

import java.util.HashMap;
import java.util.Map;

public class DescriptorProto {
    Map<String, Descriptors.FileDescriptor> filesByName = new HashMap<>();





    protected void parsingdependencies(PluginProtos.CodeGeneratorRequest request) throws Descriptors.DescriptorValidationException {
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

}
