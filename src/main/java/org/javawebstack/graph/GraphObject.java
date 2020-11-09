package org.javawebstack.graph;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class GraphObject implements GraphElement {

    private final Map<String, GraphElement> entries = new HashMap<>();

    public GraphObject setNull(String key){
        set(key, GraphNull.INSTANCE);
        return this;
    }

    public GraphObject set(String key, GraphElement value){
        if(value == null)
            value = GraphNull.INSTANCE;
        entries.put(key, value);
        return this;
    }

    public GraphObject set(String key, Number value){
        if(value == null)
            return setNull(key);
        return set(key, new GraphPrimitive(value));
    }

    public GraphObject set(String key, Boolean value){
        if(value == null)
            return setNull(key);
        return set(key, new GraphPrimitive(value));
    }

    public GraphObject set(String key, String value){
        if(value == null)
            return setNull(key);
        return set(key, new GraphPrimitive(value));
    }

    public GraphObject remove(String key){
        entries.remove(key);
        return this;
    }

    public GraphObject clear(){
        entries.clear();
        return this;
    }

    public boolean isObject(){
        return true;
    }

    public GraphObject object() {
        return this;
    }

    public GraphElement get(String key){
        return entries.get(key);
    }

    public boolean has(String key){
        return entries.containsKey(key);
    }

    public int size(){
        return entries.size();
    }

    public GraphArray array(){
        GraphArray array = new GraphArray();
        for(int i=0; i<size(); i++){
            if(!has(String.valueOf(i)))
                return null;
            array.add(get(String.valueOf(i)));
        }
        return array;
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

    public static GraphObject fromJson(JsonObject object){
        GraphObject o = new GraphObject();
        object.keySet().forEach(k -> o.set(k, GraphElement.fromJson(object.get(k))));
        return o;
    }

    public Type getType() {
        return Type.OBJECT;
    }

    public GraphObject forEach(BiConsumer<String, GraphElement> biConsumer){
        entries.forEach(biConsumer);
        return this;
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

}
