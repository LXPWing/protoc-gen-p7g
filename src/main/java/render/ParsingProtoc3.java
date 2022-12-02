package render;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.MapField;
import com.google.protobuf.compiler.PluginProtos;
import dto.Message;
import utils.ConvertUtil;

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


    public static void getFields(Map<String, Descriptors.FileDescriptor> map) {
        for(Map.Entry<String, Descriptors.FileDescriptor> entry:map.entrySet()){
            String fileName = entry.getKey();
            Descriptors.FileDescriptor fileDescriptor = entry.getValue();
            List<Descriptors.Descriptor> messageType  = fileDescriptor.getMessageTypes();
//            System.out.println("====");
            for (Descriptors.Descriptor d : messageType) {
//                System.out.println(d.getFields());
                List<Descriptors.FieldDescriptor> list = d.getFields();
                List<Descriptors.EnumDescriptor> enumTypes = d.getEnumTypes();
                List<Descriptors.Descriptor> nestedTypes = d.getNestedTypes();
                nestedTypes.forEach( i -> {
                    i.getNestedTypes().forEach(y -> {
                        System.out.println(y.getName());
                    });
                });

//                for (Descriptors.EnumDescriptor e:enumTypes){
//                    System.out.println(e.getName());
//                    System.out.println(e.getValues());
//                }

                for (Descriptors.FieldDescriptor l : list){
                    //System.out.println(list.size());
                    //System.out.println(l.getJavaType());
                    if(l.isMapField()) {

//                        Descriptors.Descriptor messageType1 = l.getMessageType();
//                        List<Descriptors.Descriptor> nestedTypes = messageType1.getNestedTypes();
//                        nestedTypes.forEach(n -> {
//                            List<Descriptors.FieldDescriptor> fields = n.getFields();
//                            fields.forEach(f -> {
//                                System.out.println(f.getMessageType().getName());
//                            });
//                        });
//
//                        System.out.println(l.getJsonName());
//                        //System.out.println("=====");
//                        List<Descriptors.FieldDescriptor> fields = messageType1.getFields();
//                        //System.out.println(messageType1.getFields());
//                        fields.forEach(item -> {
//                           // Descriptors.Descriptor messageType2 = item.getMessageType();
//                            System.out.println(item.getJsonName());
//                            Descriptors.Descriptor containingType = item.getContainingType();
//                            List<Descriptors.FieldDescriptor> fields1 = containingType.getFields();
//                            fields1.forEach(i -> {
//                                System.out.println(i.getName());
//                            });
//                            //System.out.println(item.getContainingType());
                            //item.get
                            //MapField.newMapField(item)
                            //System.out.println(item.getFullName());
                            //System.out.println();
                    //});
                        //System.out.println("=====");
                        //System.out.println(l.getMessageType().getName());
                    }
                    //System.out.println(l.isMapField());
                    //if (l.getMessageType() != null) System.out.println(l.getMessageType());
                }
                //System.out.println(d.getFullName());
                //System.out.println(d.getName());
            }
            //System.out.println("====");
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
                .filter(item -> !item.isMapField() )
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

    @Override
    public void renderType(Descriptors.FieldDescriptor fd) {

    }
}
