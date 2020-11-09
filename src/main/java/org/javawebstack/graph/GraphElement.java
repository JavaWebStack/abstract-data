package org.javawebstack.graph;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public interface GraphElement {
    Type getType();
    default boolean isPrimitive(){
        return false;
    }
    default boolean isObject(){
        return false;
    }
    default boolean isArray(){
        return false;
    }
    default boolean isNull(){
        return false;
    }
    default boolean isNumber(){
        return false;
    }
    default boolean isBoolean(){
        return false;
    }
    default boolean isString(){
        return false;
    }
    default GraphPrimitive primitive(){
        return null;
    }
    default GraphArray array(){
        return null;
    }
    default GraphObject object(){
        return null;
    }
    default String string(){
        if(!isString())
            return null;
        return primitive().string();
    }
    default Boolean bool(){
        if(!isBoolean())
            return null;
        return primitive().bool();
    }
    default Number number(){
        if(!isNumber())
            return null;
        return primitive().number();
    }
    JsonElement toJson();

    default Map<String, String> toFormData(){
        Map<String[], Object> tree = toTree();
        Map<String, String> data = new HashMap<>();
        for(String[] key : tree.keySet()){
            if(key == null || key.length == 0)
                continue;
            StringBuilder sb = new StringBuilder(key[0]);
            for(int i=1; i<key.length; i++)
                sb.append('[').append(key[i]).append(']');
            Object value = tree.get(key);
            data.put(sb.toString(), value == null ? "" : value.toString());
        }
        return data;
    }

    Map<String[], Object> toTree();

    static GraphElement fromJson(JsonElement element){
        if(element == null)
            return null;
        if(element.isJsonArray())
            return GraphArray.fromJson(element.getAsJsonArray());
        if(element.isJsonObject())
            return GraphObject.fromJson(element.getAsJsonObject());
        if(element.isJsonPrimitive())
            return GraphPrimitive.fromJson(element.getAsJsonPrimitive());
        return GraphNull.INSTANCE;
    }

    static GraphElement fromTree(Map<String[], Object> tree){
        GraphObject object = new GraphObject();
        for(String[] key : tree.keySet()){
            GraphObject current = object;
            Object value = tree.get(key);
            int offset = 0;
            while (key.length - offset > 1){
                if(current.has(key[offset])){
                    current = current.get(key[offset]).object();
                }else{
                    GraphObject n = new GraphObject();
                    current.set(key[offset], n);
                    current = n;
                }
                offset++;
            }
            current.set(key[offset], GraphPrimitive.from(value));
        }
        return object;
    }

    static GraphElement fromFormData(Map<String, String> formData){
        Map<String[], Object> tree = new HashMap<>();
        for(String k : formData.keySet()){
            String[] key = k.split("\\[");
            for(int i=1; i<key.length; i++)
                key[i] = key[i].substring(0, key[i].length()-1);
            tree.put(key, formData.get(k));
        }
        return fromTree(tree);
    }

    enum Type {
        NULL,
        STRING,
        NUMBER,
        BOOLEAN,
        OBJECT,
        ARRAY;
        public boolean isPrimitive(){
            return this == NUMBER || this == BOOLEAN || this == STRING;
        }
    }
}
