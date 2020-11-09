package org.javawebstack.graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.javawebstack.querystring.QueryString;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GraphElement {
    Type getType();

    default boolean isPrimitive() {
        return false;
    }

    default boolean isObject() {
        return false;
    }

    default boolean isArray() {
        return false;
    }

    default boolean isNull() {
        return false;
    }

    default boolean isNumber() {
        return false;
    }

    default boolean isBoolean() {
        return false;
    }

    default boolean isString() {
        return false;
    }

    default GraphPrimitive primitive() {
        return null;
    }

    default GraphArray array() {
        return null;
    }

    default GraphObject object() {
        return null;
    }

    default String string() {
        if (!isString())
            return null;
        return primitive().string();
    }

    default Boolean bool() {
        if (!isBoolean())
            return null;
        return primitive().bool();
    }

    default Number number() {
        if (!isNumber())
            return null;
        return primitive().number();
    }

    JsonElement toJson();
    default String toJsonString(boolean pretty){
        if(pretty)
            return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
        return new Gson().toJson(toJson());
    }
    default String toJsonString(){
        return toJsonString(false);
    }

    Object toAbstractObject();

    default String toYaml(boolean pretty){
        Yaml yaml;
        if(pretty){
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            yaml = new Yaml(options);
        }else{
            yaml = new Yaml();
        }
        return yaml.dump(toAbstractObject());
    }

    default String toYaml(){
        return toYaml(true);
    }

    static GraphElement fromYaml(String source, boolean singleRoot){
        Yaml yaml = new Yaml();
        Object object = yaml.load(source);
        if(singleRoot && object instanceof List){
            List<Object> list = (List<Object>) object;
            if(list.size() == 0){
                object = new HashMap<>();
            }else{
                object = list.get(0);
            }
        }
        return fromAbstractObject(object);
    }

    static GraphElement fromYaml(String source){
        return fromYaml(source, false);
    }

    static GraphElement fromAbstractObject(Object object){
        if(object == null)
            return GraphNull.INSTANCE;
        if(object instanceof List){
            List<Object> list = (List<Object>) object;
            GraphArray array = new GraphArray();
            list.forEach(e -> array.add(fromAbstractObject(e)));
            return array;
        }
        if(object instanceof Map){
            Map<String, Object> map = (Map<String, Object>) object;
            GraphObject graphObject = new GraphObject();
            map.forEach((k,v) -> graphObject.set(k, fromAbstractObject(v)));
            return graphObject;
        }
        if(object instanceof Number)
            return new GraphPrimitive((Number) object);
        if(object instanceof String)
            return new GraphPrimitive((String) object);
        if(object instanceof Boolean)
            return new GraphPrimitive((Boolean) object);
        return GraphNull.INSTANCE;
    }

    default QueryString toFormData() {
        Map<String[], Object> tree = toTree();
        QueryString data = new QueryString();
        for (String[] key : tree.keySet())
            data.set(key, tree.get(key) == null ? "" : tree.get(key).toString());
        return data;
    }

    default String toFormDataString() {
        return toFormData().toString();
    }

    Map<String[], Object> toTree();

    static GraphElement fromJson(JsonElement element) {
        if (element == null)
            return null;
        if (element.isJsonArray())
            return GraphArray.fromJson(element.getAsJsonArray());
        if (element.isJsonObject())
            return GraphObject.fromJson(element.getAsJsonObject());
        if (element.isJsonPrimitive())
            return GraphPrimitive.fromJson(element.getAsJsonPrimitive());
        return GraphNull.INSTANCE;
    }

    static GraphElement fromJson(String json) {
        return fromJson(new Gson().fromJson(json, JsonElement.class));
    }

    static GraphElement fromTree(Map<String[], Object> tree) {
        GraphObject object = new GraphObject();
        for (String[] key : tree.keySet()) {
            GraphObject current = object;
            Object value = tree.get(key);
            int offset = 0;
            while (key.length - offset > 1) {
                if (current.has(key[offset])) {
                    current = current.get(key[offset]).object();
                } else {
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

    static GraphElement fromFormData(Map<String, String> formData) {
        return fromFormData(new QueryString(formData));
    }

    static GraphElement fromFormData(QueryString formData) {
        return fromTree(formData.toTree());
    }

    static GraphElement fromFormData(String formData) {
        return fromFormData(new QueryString(formData));
    }

    enum Type {
        NULL,
        STRING,
        NUMBER,
        BOOLEAN,
        OBJECT,
        ARRAY;

        public boolean isPrimitive() {
            return this == NUMBER || this == BOOLEAN || this == STRING;
        }
    }
}
