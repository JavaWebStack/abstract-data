package org.javawebstack.abstractdata;

import org.javawebstack.abstractdata.exception.AbstractCoercingException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class AbstractPrimitive implements AbstractElement {
    private final Object value;

    public AbstractPrimitive(Number value) {
        if(value == null)
            throw new NullPointerException("AbstractPrimitive value can not be null");
        this.value = value;
    }

    public AbstractPrimitive(Boolean value) {
        if(value == null)
            throw new NullPointerException("AbstractPrimitive value can not be null");
        this.value = value;
    }

    public AbstractPrimitive(String value) {
        if(value == null)
            throw new NullPointerException("AbstractPrimitive value can not be null");
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

    public String string() throws AbstractCoercingException {
        return string(false);
    }

    public String string(boolean strict) throws AbstractCoercingException {
        if(!(value instanceof String)) {
            if(strict)
                throw new AbstractCoercingException(Type.STRING, getType());
            switch (getType()) {
                case BOOLEAN:
                    return bool().toString();
                case NUMBER:
                    return number().toString();
                default:
                    throw new AbstractCoercingException(Type.STRING, getType());
            }
        }
        return (String) value;
    }

    public Number number() throws AbstractCoercingException {
        return number(false);
    }

    public Number number(boolean strict) throws AbstractCoercingException {
        if(!(value instanceof Number)) {
            if(strict)
                throw new AbstractCoercingException(Type.NUMBER, getType());
            switch (getType()) {
                case BOOLEAN:
                    return ((Boolean) value) ? 1 : 0;
                case STRING: {
                    try {
                        return NumberFormat.getNumberInstance().parse((String) value);
                    } catch (ParseException e) {
                        throw new AbstractCoercingException(Type.NUMBER, this);
                    }
                }
                default:
                    throw new AbstractCoercingException(Type.NUMBER, getType());
            }
        }
        return (Number) value;
    }

    public Boolean bool() throws AbstractCoercingException {
        return bool(false);
    }

    public Boolean bool(boolean strict) throws AbstractCoercingException {
        if(!(value instanceof Boolean)) {
            if(strict)
                throw new AbstractCoercingException(Type.BOOLEAN, getType());
            switch (getType()) {
                case STRING: {
                    switch (((String) value)) {
                        case "1":
                        case "true":
                        case "yes":
                        case "y":
                            return true;
                        case "0":
                        case "false":
                        case "no":
                        case "n":
                            return false;
                        default:
                            throw new AbstractCoercingException(Type.BOOLEAN, this);
                    }
                }
                case NUMBER: {
                    long l = ((Number) value).longValue();
                    if(l == 0)
                        return false;
                    if(l == 1)
                        return true;
                    throw new AbstractCoercingException(Type.BOOLEAN, this);
                }
                default:
                    throw new AbstractCoercingException(Type.BOOLEAN, getType());
            }
        }
        return (Boolean) value;
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

    public boolean equals(Object obj, boolean strict) {
        if (obj == null)
            return false;
        if (!(obj instanceof AbstractPrimitive))
            return false;

        AbstractPrimitive primitive = (AbstractPrimitive) obj;
        if (isBoolean() && primitive.isBoolean())
            return bool(strict).equals(primitive.bool(strict));
        if (isNumber() && primitive.isNumber())
            return number(strict).equals(primitive.number(strict));
        if (isString() && primitive.isString())
            return string(strict).equals(primitive.string(strict));

        return false;
    }

    public boolean equals (Object obj) {
        return equals(obj, false);
    }
}
