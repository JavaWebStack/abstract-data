package org.javawebstack.abstractdata;

import java.util.HashMap;
import java.util.Map;

public class AbstractPrimitive implements AbstractElement {
    private final Object value;

    public AbstractPrimitive(Number value) {
        this.value = value;
    }

    public AbstractPrimitive(Boolean value) {
        this.value = value;
    }

    public AbstractPrimitive(String value) {
        this.value = value;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isPrimitive() {
        return true;
    }

    public AbstractPrimitive primitive() {
        return this;
    }

    public String string() {
        return (String) value;
    }

    public Number number() {
        return (Number) value;
    }

    public Boolean bool() {
        if (isBoolean())
            return (Boolean) value;
        return null;
    }

    public Object value() {
        return value;
    }

    public Object toObject() {
        return value;
    }

    public Map<String[], Object> toTree() {
        Map<String[], Object> tree = new HashMap<>();
        tree.put(new String[0], value);
        return tree;
    }

    public static AbstractPrimitive from(Object object) {
        if (object instanceof Number)
            return new AbstractPrimitive((Number) object);
        if (object instanceof Boolean)
            return new AbstractPrimitive((Boolean) object);
        if (object instanceof String)
            return new AbstractPrimitive((String) object);
        return null;
    }

    public Type getType() {
        if (isBoolean())
            return Type.BOOLEAN;
        if (isNumber())
            return Type.NUMBER;
        if (isString())
            return Type.STRING;
        return null;
    }

    public String toString() {
        return value.toString();
    }

    public AbstractElement clone() {
        return from(value);
    }

}
