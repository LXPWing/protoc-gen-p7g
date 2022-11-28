package render;

import java.util.Map;

public interface IProtocl<T> {
    void getFields(T t);

    void doAnalysisProtoFile(Object request);

    void getMapField(T t);

    void getEnumField(T t);
}
