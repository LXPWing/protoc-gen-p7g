package dto;

import java.util.List;
import java.util.Map;

public abstract class Message {
    private String packagePath;

    private String className;

    private List<String> importInfo;

    private Map<String, String> messageTypeInfo;

    private Map<String, List<Object>> embedEnumInfo;

    private Map<String, Map<Object, Object>> mapInfo;

    private Map<String, Message> embedMessageInfo;

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getImportInfo() {
        return importInfo;
    }

    public void setImportInfo(List<String> importInfo) {
        this.importInfo = importInfo;
    }

    public Map<String, String> getMessageTypeInfo() {
        return messageTypeInfo;
    }

    public void setMessageTypeInfo(Map<String, String> messageTypeInfo) {
        this.messageTypeInfo = messageTypeInfo;
    }

    public Map<String, List<Object>> getEmbedEnumInfo() {
        return embedEnumInfo;
    }

    public void setEmbedEnumInfo(Map<String, List<Object>> embedEnumInfo) {
        this.embedEnumInfo = embedEnumInfo;
    }

    public Map<String, Map<Object, Object>> getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(Map<String, Map<Object, Object>> mapInfo) {
        this.mapInfo = mapInfo;
    }

    public Map<String, Message> getEmbedMessageInfo() {
        return embedMessageInfo;
    }

    public void setEmbedMessageInfo(Map<String, Message> embedMessageInfo) {
        this.embedMessageInfo = embedMessageInfo;
    }
}
