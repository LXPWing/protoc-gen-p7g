package dto;

import java.util.List;
import java.util.Map;

public class JavaMessage implements Message {
    private Map<String, String> javaType;

    private String packageInfo;

    private String className;

    private List<String> importInfo;

    private Map<String, List<Object>> enumInfo;

    private Map<String, Map<Object, Object>> mapInfo;

    private Map<String, JavaMessage> embedJavaMessage;

    public JavaMessage() {}
}
