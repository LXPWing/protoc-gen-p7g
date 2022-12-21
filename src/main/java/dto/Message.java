package dto;

import com.google.protobuf.Descriptors;

import java.util.List;
import java.util.Map;

public abstract class Message {
    private String packagePath;

    private String className;

    private List<String> importInfo;

    private Map<String, String> messageTypeInfo;

    private Map<String, List<Descriptors.EnumValueDescriptor>> enumInfo;

    private Map<String, MapKV> mapInfo;

    private Map<String, Message> embedMessageInfo;

    private Map<String, String> listInfo;

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

    public Map<String, List<Descriptors.EnumValueDescriptor>> getEnumInfo() {
        return enumInfo;
    }

    public void setEnumInfo(Map<String, List<Descriptors.EnumValueDescriptor>> enumInfo) {
        this.enumInfo = enumInfo;
    }

    public Map<String, Message> getEmbedMessageInfo() {
        return embedMessageInfo;
    }

    public void setEmbedMessageInfo(Map<String, Message> embedMessageInfo) {
        this.embedMessageInfo = embedMessageInfo;
    }
    public Map<String, String> getListInfo() {
        return listInfo;
    }

    public void setListInfo(Map<String, String> listInfo) {
        this.listInfo = listInfo;
    }

    public Map<String, MapKV> getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(Map<String, MapKV> mapInfo) {
        this.mapInfo = mapInfo;
    }

    @Override
    public String toString() {
        return "Message{" +
                "packagePath='" + packagePath + '\'' +
                ", className='" + className + '\'' +
                ", importInfo=" + importInfo +
                ", messageTypeInfo=" + messageTypeInfo +
                ", enumInfo=" + enumInfo +
                ", mapInfo=" + mapInfo +
                ", embedMessageInfo=" + embedMessageInfo +
                ", listInfo=" + listInfo +
                '}';
    }
}
