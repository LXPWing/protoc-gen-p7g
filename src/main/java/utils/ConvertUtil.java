package utils;

import com.google.protobuf.Descriptors;

public class ConvertUtil {

    public static final String convertToString(Descriptors.FieldDescriptor.JavaType javaType) {
        if(javaType.equals(Descriptors.FieldDescriptor.JavaType.STRING)){
            return "String";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.BYTE_STRING)) {
            return "Byte";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.BOOLEAN)) {
            return "Boolean";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.DOUBLE)) {
            return "Double";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.INT)) {
            return "Integer";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.LONG)) {
            return "Long";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.FLOAT)) {
            return "Float";
        } else if(javaType.equals(Descriptors.FieldDescriptor.JavaType.ENUM)) {
            return "Enum";
        } else {
            return "Message";
        }
    }

    public static final String convertToString(Descriptors.FieldDescriptor.Type type){
        return convertToString(type.getJavaType());
    }
}
