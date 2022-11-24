package dto;

import java.util.List;
import java.util.Map;

public class JavaMessage implements Message {
    private Map<String, String> javaType;

    private String packageInfo;

    private String className;

    private List<String> importInfo;

    private Map<String, List<Object>> embedEnumInfo;

    private Map<String, Map<Object, Object>> mapInfo;

    private Map<String, JavaMessage> embedJavaMessage;

    public JavaMessage() {}

    public Map<String, String> getJavaType() {
        return javaType;
    }

    public void setJavaType(Map<String, String> javaType) {
        this.javaType = javaType;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
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

    public Map<String, JavaMessage> getEmbedJavaMessage() {
        return embedJavaMessage;
    }

    public void setEmbedJavaMessage(Map<String, JavaMessage> embedJavaMessage) {
        this.embedJavaMessage = embedJavaMessage;
    }
}
