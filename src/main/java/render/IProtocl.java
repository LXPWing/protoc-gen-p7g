package render;

import java.util.Map;

public interface IProtocl<T> {
    void getFields(Map<String, T> map);

    void doAnalysisProtoFile(Object request);


}
