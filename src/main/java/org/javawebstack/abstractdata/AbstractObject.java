package org.javawebstack.abstractdata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class AbstractObject implements AbstractElement {

    private final Map<String, AbstractElement> entries = new HashMap<>();

    public AbstractObject setNull(String key){
        set(key, AbstractNull.INSTANCE);
        return this;
    }

    public AbstractObject set(String key, AbstractElement value){
        if(value == null)
            value = AbstractNull.INSTANCE;
        entries.put(key, value);
        return this;
    }

    public AbstractObject set(String key, Number value){
        if(value == null)
            return setNull(key);
        return set(key, new AbstractPrimitive(value));
    }

    public AbstractObject set(String key, Boolean value){
        if(value == null)
            return setNull(key);
        return set(key, new AbstractPrimitive(value));
    }

    public AbstractObject set(String key, String value){
        if(value == null)
            return setNull(key);
        return set(key, new AbstractPrimitive(value));
    }

    public AbstractObject remove(String key){
        entries.remove(key);
        return this;
    }

    public AbstractObject clear(){
        entries.clear();
        return this;
    }

    public boolean isObject(){
        return true;
    }

    public AbstractObject object() {
        return this;
    }

    public AbstractElement get(String key){
        return entries.get(key);
    }

    public AbstractElement get(String key, AbstractElement orElse) {
        AbstractElement value = get(key);
        return (has(key) && !value.isNull()) ? value : orElse;
    }

    public boolean has(String key){
        return entries.containsKey(key);
    }

    public int size(){
        return entries.size();
    }

    public AbstractArray array(){
        AbstractArray array = new AbstractArray();
        for(int i=0; i<size(); i++){
            if(!has(String.valueOf(i)))
                return null;
            array.add(get(String.valueOf(i)));
        }
        return array;
    }

    public AbstractObject object(String key) {
        return get(key).object();
    }

    public AbstractObject object(String key, AbstractObject orElse) {
        return get(key, orElse).object();
    }

    public AbstractArray array(String key) {
        return get(key).array();
    }

    public AbstractArray array(String key, AbstractArray orElse) {
        return get(key, orElse).array();
    }

    public AbstractPrimitive primitive(String key) {
        return get(key).primitive();
    }

    public AbstractPrimitive primitive(String key, AbstractPrimitive orElse) {
        return get(key, orElse).primitive();
    }

    public String string(String key) {
        return get(key).string();
    }

    public String string(String key, String orElse) {
        return get(key, new AbstractPrimitive(orElse)).string();
    }

    public Boolean bool(String key) {
        return get(key).bool();
    }

    public Boolean bool(String key, Boolean orElse) {
        return get(key, new AbstractPrimitive(orElse)).bool();
    }

    public Number number(String key) {
        return get(key).number();
    }

    public Number number(String key, Number orElse) {
        return get(key, new AbstractPrimitive(orElse)).number();
    }

    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        entries.forEach((k,v) -> object.add(k, v.toJson()));
        return object;
    }

    public Object toAbstractObject() {
        Map<String, Object> map = new HashMap<>();
        forEach((k,v) -> map.put(k, v.toAbstractObject()));
        return map;
    }

    public static AbstractObject fromJson(JsonObject object){
        AbstractObject o = new AbstractObject();
        object.entrySet().stream().map(Map.Entry::getKey).forEach(k -> o.set(k, AbstractElement.fromJson(object.get(k))));
        return o;
    }

    public Type getType() {
        return Type.OBJECT;
    }

    public AbstractObject forEach(BiConsumer<String, AbstractElement> biConsumer){
        entries.forEach(biConsumer);
        return this;
    }

    public <T> T fill(Object object) {
        Class clazz = object.getClass();

        for (Field field : clazz.getFields()) {
            if (has(field.getName())) {
                field.setAccessible(true);

                try {
                    field.set(object, get(field.getName()).toAbstractObject());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return (T) object;
    }

    public Map<String[], Object> toTree(){
        Map<String[], Object> tree = new HashMap<>();
        forEach((key, value) -> value.toTree().forEach((keys, v) -> {
            String[] k = new String[keys.length+1];
            k[0] = key;
            System.arraycopy(keys, 0, k, 1, keys.length);
            tree.put(k, v);
        }));
        return tree;
    }

    public Set<String> keys(){
        return entries.keySet();
    }

    public AbstractArray values(){
        return AbstractArray.fromList(entries.values());
    }

    public AbstractElement clone() {
        AbstractObject object = new AbstractObject();
        forEach((k, v) -> object.set(k, v.clone()));
        return object;
    }

}
