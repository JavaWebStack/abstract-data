package org.javawebstack.abstractdata;

import org.bson.BsonValue;
import org.javawebstack.abstractdata.bson.BsonConverter;
import org.javawebstack.abstractdata.bson.BsonTypeAdapter;
import org.javawebstack.abstractdata.exception.AbstractCoercingException;
import org.javawebstack.abstractdata.json.JsonDumper;
import org.javawebstack.abstractdata.json.JsonParser;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.abstractdata.yaml.YamlDumper;
import org.javawebstack.abstractdata.yaml.YamlParser;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.text.ParseException;
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

    default AbstractPrimitive primitive() throws AbstractCoercingException {
        throw new AbstractCoercingException("PRIMITIVE", getType());
    }

    default AbstractArray array() throws AbstractCoercingException {
        return array(false);
    }

    default AbstractObject object() throws AbstractCoercingException {
        return object(false);
    }

    default AbstractArray array(boolean strict) throws AbstractCoercingException {
        throw new AbstractCoercingException("ARRAY", getType());
    }

    default AbstractObject object(boolean strict) throws AbstractCoercingException {
        throw new AbstractCoercingException("OBJECT", getType());
    }

    default String string() throws AbstractCoercingException {
        return string(false);
    }

    default Boolean bool() throws AbstractCoercingException {
        return bool(false);
    }

    default Number number() throws AbstractCoercingException {
        return number(false);
    }

    default String string(boolean strict) throws AbstractCoercingException {
        return primitive().string(strict);
    }

    default Boolean bool(boolean strict) throws AbstractCoercingException {
        return primitive().bool(strict);
    }

    default Number number(boolean strict) throws AbstractCoercingException {
        return primitive().number(strict);
    }

    default BsonValue toBson() {
        return new BsonConverter().toBson(this);
    }

    default byte[] toBsonBytes() {
        return (byte[]) new BsonTypeAdapter().fromAbstract(null, this, byte[].class);
    }

    default String toJsonString(boolean pretty) {
        return new JsonDumper().setPretty(pretty).dump(this);
    }

    default String toJsonString() {
        return toJsonString(false);
    }

    Object toObject();

    default String toYaml(boolean pretty) {
        return new YamlDumper().setPretty(pretty).dump(this);
    }

    default String toYaml() {
        return toYaml(true);
    }

    static AbstractElement fromYaml(String source, boolean singleRoot) {
        try {
            return new YamlParser().setSingleRoot(singleRoot).parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    static AbstractElement fromYaml(String source) {
        return fromYaml(source, false);
    }

    @Deprecated
    static AbstractElement fromAbstractObject(Object object) {
        return fromObject(object);
    }

    static AbstractElement fromObject(Object object) {
        if (object == null)
            return AbstractNull.VALUE;
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
        return AbstractNull.VALUE;
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

    static AbstractElement fromBson(BsonValue value) {
        return new BsonConverter().toAbstract(value);
    }

    static AbstractElement fromBson(byte[] value) {
        return new BsonTypeAdapter().toAbstract(null, value);
    }

    AbstractElement clone();

    Map<String[], Object> toTree();

    static AbstractElement fromJson(String json) {
        try {
            return new JsonParser().parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

    boolean equals(Object obj, boolean strict);
}
