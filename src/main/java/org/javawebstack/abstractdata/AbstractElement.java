package org.javawebstack.abstractdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.javawebstack.abstractdata.util.QueryString;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AbstractElement {

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

    default AbstractPrimitive primitive() {
        return null;
    }

    default AbstractArray array() {
        return null;
    }

    default AbstractObject object() {
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

    default String toJsonString(boolean pretty) {
        if (pretty)
            return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(toJson());
        return new GsonBuilder().disableHtmlEscaping().create().toJson(toJson());
    }

    default String toJsonString() {
        return toJsonString(false);
    }

    Object toAbstractObject();

    default String toYaml(boolean pretty) {
        Yaml yaml;
        if (pretty) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            yaml = new Yaml(options);
        } else {
            yaml = new Yaml();
        }
        return yaml.dump(toAbstractObject());
    }

    default String toYaml() {
        return toYaml(true);
    }

    static AbstractElement fromYaml(String source, boolean singleRoot) {
        Yaml yaml = new Yaml();
        Object object = yaml.load(source);
        if (singleRoot && object instanceof List) {
            List<Object> list = (List<Object>) object;
            if (list.size() == 0) {
                object = new HashMap<>();
            } else {
                object = list.get(0);
            }
        }
        return fromAbstractObject(object);
    }

    static AbstractElement fromYaml(String source) {
        return fromYaml(source, false);
    }

    static AbstractElement fromAbstractObject(Object object) {
        if (object == null)
            return AbstractNull.INSTANCE;
        if (object instanceof List) {
            List<Object> list = (List<Object>) object;
            AbstractArray array = new AbstractArray();
            list.forEach(e -> array.add(fromAbstractObject(e)));
            return array;
        }
        if (object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            AbstractObject graphObject = new AbstractObject();
            map.forEach((k, v) -> graphObject.set(k, fromAbstractObject(v)));
            return graphObject;
        }
        if (object instanceof Number)
            return new AbstractPrimitive((Number) object);
        if (object instanceof String)
            return new AbstractPrimitive((String) object);
        if (object instanceof Boolean)
            return new AbstractPrimitive((Boolean) object);
        return AbstractNull.INSTANCE;
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

    AbstractElement clone();

    Map<String[], Object> toTree();

    static AbstractElement fromJson(JsonElement element) {
        if (element == null)
            return null;
        if (element.isJsonArray())
            return AbstractArray.fromJson(element.getAsJsonArray());
        if (element.isJsonObject())
            return AbstractObject.fromJson(element.getAsJsonObject());
        if (element.isJsonPrimitive())
            return AbstractPrimitive.fromJson(element.getAsJsonPrimitive());
        return AbstractNull.INSTANCE;
    }

    static AbstractElement fromJson(String json) {
        return fromJson(new GsonBuilder().create().fromJson(json, JsonElement.class));
    }

    static AbstractElement fromTree(Map<String[], Object> tree) {
        AbstractObject object = new AbstractObject();
        for (String[] key : tree.keySet()) {
            AbstractObject current = object;
            Object value = tree.get(key);
            int offset = 0;
            while (key.length - offset > 1) {
                if (current.has(key[offset])) {
                    current = current.get(key[offset]).object();
                } else {
                    AbstractObject n = new AbstractObject();
                    current.set(key[offset], n);
                    current = n;
                }
                offset++;
            }
            current.set(key[offset], AbstractPrimitive.from(value));
        }
        return object;
    }

    static AbstractElement fromFormData(Map<String, String> formData) {
        return fromFormData(new QueryString(formData));
    }

    static AbstractElement fromFormData(QueryString formData) {
        return fromTree(formData.toTree());
    }

    static AbstractElement fromFormData(String formData) {
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
