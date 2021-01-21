package org.javawebstack.abstractdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AbstractArray implements AbstractElement, Iterable<AbstractElement> {

    private final List<AbstractElement> elements = new ArrayList<>();

    public boolean isArray() {
        return true;
    }

    public AbstractArray array() {
        return this;
    }

    public AbstractArray add(AbstractElement element) {
        if (element == null)
            element = AbstractNull.INSTANCE;
        elements.add(element);
        return this;
    }

    public AbstractArray addNull() {
        return add(AbstractNull.INSTANCE);
    }

    public AbstractArray add(Number value) {
        if (value == null)
            return addNull();
        return add(new AbstractPrimitive(value));
    }

    public AbstractArray add(Boolean value) {
        if (value == null)
            return addNull();
        return add(new AbstractPrimitive(value));
    }

    public AbstractArray add(String value) {
        if (value == null)
            return addNull();
        return add(new AbstractPrimitive(value));
    }

    public AbstractArray setNull(int i){
        elements.set(i, AbstractNull.INSTANCE);
        return this;
    }

    public AbstractArray set(int i, AbstractElement element){
        elements.set(i, element);
        return this;
    }

    public AbstractArray set(int i, Number value){
        if(value == null)
            return setNull(i);
        return set(i, new AbstractPrimitive(value));
    }

    public AbstractArray set(int i, Boolean value){
        if(value == null)
            return setNull(i);
        return set(i, new AbstractPrimitive(value));
    }

    public AbstractArray set(int i, String value){
        if(value == null)
            return setNull(i);
        return set(i, new AbstractPrimitive(value));
    }

    public AbstractArray remove(int i){
        elements.remove(i);
        return this;
    }

    public AbstractElement get(int i){
        return elements.get(i);
    }

    public Stream<AbstractElement> stream(){
        return elements.stream();
    }

    public int size() {
        return elements.size();
    }

    public AbstractArray clear() {
        elements.clear();
        return this;
    }

    public Iterator<AbstractElement> iterator() {
        return elements.iterator();
    }

    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        elements.forEach(e -> array.add(e.toJson()));
        return array;
    }

    public Object toAbstractObject() {
        List<Object> list = new ArrayList<>();
        elements.forEach(e -> list.add(e.toAbstractObject()));
        return list;
    }

    public static AbstractArray fromJson(JsonArray array){
        AbstractArray a = new AbstractArray();
        array.forEach(e -> a.add(AbstractElement.fromJson(e)));
        return a;
    }

    public Type getType() {
        return Type.ARRAY;
    }

    public AbstractObject object(){
        AbstractObject o = new AbstractObject();
        for(int i=0; i<size(); i++)
            o.set(String.valueOf(i), get(i));
        return o;
    }

    public Map<String[], Object> toTree(){
        return object().toTree();
    }

}
