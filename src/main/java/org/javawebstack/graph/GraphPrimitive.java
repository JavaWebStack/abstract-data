package org.javawebstack.graph;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;

public class GraphPrimitive implements GraphElement {
    private final Object value;
    public GraphPrimitive(Number value){
        this.value = value;
    }
    public GraphPrimitive(Boolean value){
        this.value = value;
    }
    public GraphPrimitive(String value){
        this.value = value;
    }
    public boolean isNumber(){
        return value instanceof Number;
    }
    public boolean isString(){
        return value instanceof String;
    }
    public boolean isBoolean(){
        return value instanceof Boolean;
    }
    public boolean isPrimitive(){
        return true;
    }
    public GraphPrimitive primitive() {
        return this;
    }

    public String string(){
        return (String) value;
    }
    public Number number(){
        return (Number) value;
    }
    public Boolean bool(){
        if(isBoolean())
            return (Boolean) value;
        return null;
    }
    public Object value() {
        return value;
    }

    public JsonElement toJson() {
        if(isString())
            return new JsonPrimitive(string());
        if(isBoolean())
            return new JsonPrimitive(bool());
        if(isNumber())
            return new JsonPrimitive(number());
        return null;
    }

    public Map<String[], Object> toTree() {
        Map<String[], Object> tree = new HashMap<>();
        tree.put(new String[0], value);
        return tree;
    }

    public static GraphPrimitive fromJson(JsonPrimitive primitive){
        if(primitive == null)
            return null;
        if(primitive.isNumber())
            return new GraphPrimitive(primitive.getAsNumber());
        if(primitive.isBoolean())
            return new GraphPrimitive(primitive.getAsBoolean());
        return new GraphPrimitive(primitive.getAsString());
    }

    public static GraphPrimitive from(Object object){
        if(object instanceof Number)
            return new GraphPrimitive((Number) object);
        if(object instanceof Boolean)
            return new GraphPrimitive((Boolean) object);
        if(object instanceof String)
            return new GraphPrimitive((String) object);
        return null;
    }

    public Type getType() {
        if(isBoolean())
            return Type.BOOLEAN;
        if(isNumber())
            return Type.NUMBER;
        if(isString())
            return Type.STRING;
        return null;
    }

    public String toString(){
        return value.toString();
    }

}
