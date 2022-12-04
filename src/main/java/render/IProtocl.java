package render;

import com.google.protobuf.Descriptors;

import java.util.Map;

public interface IProtocl<T> {
    //void getFields(T t);

    void getCommonFields(Descriptors.Descriptor descriptor);

    void doAnalysisProtoFile(Object request);

    void getMapField(T t);

    void getEnumField(T t);

    // void renderType(Descriptors.FieldDescriptor fd);
}
