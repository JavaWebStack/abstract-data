package org.javawebstack.graph;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GraphArray implements GraphElement, Iterable<GraphElement> {

    private final List<GraphElement> elements = new ArrayList<>();

    public boolean isArray() {
        return true;
    }

    public GraphArray array() {
        return this;
    }

    public GraphArray add(GraphElement element) {
        if (element == null)
            element = GraphNull.INSTANCE;
        elements.add(element);
        return this;
    }

    public GraphArray addNull() {
        return add(GraphNull.INSTANCE);
    }

    public GraphArray add(Number value) {
        if (value == null)
            return addNull();
        return add(new GraphPrimitive(value));
    }

    public GraphArray add(Boolean value) {
        if (value == null)
            return addNull();
        return add(new GraphPrimitive(value));
    }

    public GraphArray add(String value) {
        if (value == null)
            return addNull();
        return add(new GraphPrimitive(value));
    }

    public GraphArray setNull(int i){
        elements.set(i, GraphNull.INSTANCE);
        return this;
    }

    public GraphArray set(int i, GraphElement element){
        elements.set(i, element);
        return this;
    }

    public GraphArray set(int i, Number value){
        if(value == null)
            return setNull(i);
        return set(i, new GraphPrimitive(value));
    }

    public GraphArray set(int i, Boolean value){
        if(value == null)
            return setNull(i);
        return set(i, new GraphPrimitive(value));
    }

    public GraphArray set(int i, String value){
        if(value == null)
            return setNull(i);
        return set(i, new GraphPrimitive(value));
    }

    public GraphArray remove(int i){
        elements.remove(i);
        return this;
    }

    public GraphElement get(int i){
        return elements.get(i);
    }

    public Stream<GraphElement> stream(){
        return elements.stream();
    }

    public int size() {
        return elements.size();
    }

    public GraphArray clear() {
        elements.clear();
        return this;
    }

    public Iterator<GraphElement> iterator() {
        return elements.iterator();
    }

    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        elements.forEach(e -> array.add(e.toJson()));
        return array;
    }

    public static GraphArray fromJson(JsonArray array){
        GraphArray a = new GraphArray();
        array.forEach(e -> a.add(GraphElement.fromJson(e)));
        return a;
    }

    public Type getType() {
        return Type.ARRAY;
    }

    public GraphObject object(){
        GraphObject o = new GraphObject();
        for(int i=0; i<size(); i++)
            o.set(String.valueOf(i), get(i));
        return o;
    }

    public Map<String[], Object> toTree(){
        return object().toTree();
    }

}
