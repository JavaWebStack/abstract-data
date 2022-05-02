package org.javawebstack.abstractdata;

import org.javawebstack.abstractdata.collector.AbstractObjectCollector;
import org.javawebstack.abstractdata.exception.AbstractCoercingException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class AbstractObject implements AbstractElement {

    private final Map<String, AbstractElement> entries = new LinkedHashMap<>();

    public Set<Map.Entry<String, AbstractElement>> entries() {
        return entries.entrySet();
    }

    public AbstractObject setNull(String key) {
        set(key, AbstractNull.VALUE);
        return this;
    }

    public AbstractObject set(String key, AbstractElement value) {
        if (value == null)
            value = AbstractNull.VALUE;
        entries.put(key, value);
        return this;
    }

    public AbstractObject set(String key, Number value) {
        if (value == null)
            return setNull(key);
        return set(key, new AbstractPrimitive(value));
    }

    public AbstractObject set(String key, Boolean value) {
        if (value == null)
            return setNull(key);
        return set(key, new AbstractPrimitive(value));
    }

    public AbstractObject set(String key, String value) {
        if (value == null)
            return setNull(key);
        return set(key, new AbstractPrimitive(value));
    }

    public AbstractObject remove(String key) {
        entries.remove(key);
        return this;
    }

    public AbstractObject clear() {
        entries.clear();
        return this;
    }

    public boolean isObject() {
        return true;
    }

    public AbstractObject object(boolean strict) throws AbstractCoercingException {
        return this;
    }

    public AbstractArray array(boolean strict) throws AbstractCoercingException {
        if(strict)
            throw new AbstractCoercingException(Type.ARRAY, Type.OBJECT);
        AbstractArray array = new AbstractArray();
        for (int i = 0; i < size(); i++) {
            if (!has(String.valueOf(i)))
                throw new AbstractCoercingException(Type.ARRAY, this);
            array.add(get(String.valueOf(i)));
        }
        return array;
    }

    public AbstractElement get(String key) {
        return entries.get(key);
    }

    public AbstractElement get(String key, AbstractElement orElse) {
        AbstractElement value = get(key);
        return (has(key) && !value.isNull()) ? value : orElse;
    }

    public AbstractElement query(String query) {
        String[] q = query.split("\\.", 2);
        AbstractElement e = get(q[0]);
        if(e == null || q.length == 1)
            return e;
        if(e.isObject())
            return e.object().query(q[1]);
        if(e.isArray())
            return e.array().query(q[1]);
        return null;
    }

    public AbstractElement query(String query, AbstractElement orElse) {
        AbstractElement value = query(query);
        return (value != null && !value.isNull()) ? value : orElse;
    }

    public boolean has(String key) {
        return entries.containsKey(key);
    }

    public boolean hasString(String key) {
        return has(key) && get(key).isString();
    }

    public boolean hasNumber(String key) {
        return has(key) && get(key).isNumber();
    }

    public boolean hasBoolean(String key) {
        return has(key) && get(key).isBoolean();
    }

    public boolean hasPrimitive(String key) {
        return has(key) && get(key).isPrimitive();
    }

    public boolean hasObject(String key) {
        return has(key) && get(key).isObject();
    }

    public boolean hasArray(String key) {
        return has(key) && get(key).isArray();
    }

    public int size() {
        return entries.size();
    }

    public AbstractObject object(String key) throws AbstractCoercingException {
        return query(key, AbstractNull.VALUE).object();
    }

    public AbstractObject object(String key, AbstractObject orElse) throws AbstractCoercingException {
        return query(key, orElse).object();
    }

    public AbstractArray array(String key) throws AbstractCoercingException {
        return query(key, AbstractNull.VALUE).array();
    }

    public AbstractArray array(String key, AbstractArray orElse) throws AbstractCoercingException {
        return query(key, orElse).array();
    }

    public AbstractPrimitive primitive(String key) throws AbstractCoercingException {
        return query(key, AbstractNull.VALUE).primitive();
    }

    public AbstractPrimitive primitive(String key, AbstractPrimitive orElse) throws AbstractCoercingException {
        return query(key, orElse).primitive();
    }

    public String string(String key) throws AbstractCoercingException {
        return query(key, AbstractNull.VALUE).string();
    }

    public String string(String key, String orElse) throws AbstractCoercingException {
        return query(key, new AbstractPrimitive(orElse)).string();
    }

    public Boolean bool(String key) throws AbstractCoercingException {
        return query(key, AbstractNull.VALUE).bool();
    }

    public Boolean bool(String key, Boolean orElse) throws AbstractCoercingException {
        return query(key, new AbstractPrimitive(orElse)).bool();
    }

    public Number number(String key) throws AbstractCoercingException {
        return query(key, AbstractNull.VALUE).number();
    }

    public Number number(String key, Number orElse) throws AbstractCoercingException {
        return query(key, new AbstractPrimitive(orElse)).number();
    }

    public Object toObject() {
        Map<String, Object> map = new HashMap<>();
        forEach((k, v) -> map.put(k, v.toObject()));
        return map;
    }

    public Type getType() {
        return Type.OBJECT;
    }

    public AbstractObject forEach(BiConsumer<String, AbstractElement> biConsumer) {
        entries.forEach(biConsumer);
        return this;
    }

    public <T> T fill(Object object) {
        Class clazz = object.getClass();

        for (Field field : clazz.getFields()) {
            if (has(field.getName())) {
                field.setAccessible(true);

                try {
                    field.set(object, get(field.getName()).toObject());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return (T) object;
    }

    public Map<String[], Object> toTree() {
        Map<String[], Object> tree = new HashMap<>();
        forEach((key, value) -> value.toTree().forEach((keys, v) -> {
            String[] k = new String[keys.length + 1];
            k[0] = key;
            System.arraycopy(keys, 0, k, 1, keys.length);
            tree.put(k, v);
        }));
        return tree;
    }

    public Set<String> keys() {
        return entries.keySet();
    }

    public AbstractArray values() {
        return AbstractArray.fromList(entries.values());
    }

    public Stream<Map.Entry<String, AbstractElement>> stream() {
        return entries().stream();
    }

    public AbstractElement clone() {
        AbstractObject object = new AbstractObject();
        forEach((k, v) -> object.set(k, v.clone()));
        return object;
    }

    public static <T> Collector<T, ?, AbstractObject> collect(Function<T, String> keyFunction, Function<T, AbstractElement> valueFunction) {
        return new AbstractObjectCollector<>(keyFunction, valueFunction);
    }

}
