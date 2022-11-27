package render;

import java.util.Map;

public interface IProtocl<T> {
    void getFields(Map<String, T> map);

    void doAnalysisProtoFile(Object request);

    void getMapField(Map<String, T> map);

    void getEnumField(Map<String, T> map);
}
