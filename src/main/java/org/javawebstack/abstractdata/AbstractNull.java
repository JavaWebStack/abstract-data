package org.javawebstack.abstractdata;

import org.javawebstack.abstractdata.exception.AbstractCoercingException;

import java.util.HashMap;
import java.util.Map;

public class AbstractNull implements AbstractElement {

    public static final AbstractNull VALUE = new AbstractNull();
    @Deprecated
    public static final AbstractNull INSTANCE = VALUE;

    private AbstractNull() {
    }

    public boolean isNull() {
        return true;
    }

    public String string(boolean strict) throws AbstractCoercingException {
        return null;
    }

    public Boolean bool(boolean strict) throws AbstractCoercingException {
        return null;
    }

    public Number number(boolean strict) throws AbstractCoercingException {
        return null;
    }

    public AbstractObject object(boolean strict) throws AbstractCoercingException {
        return null;
    }

    public AbstractArray array(boolean strict) throws AbstractCoercingException {
        return null;
    }

    public Object toObject() {
        return null;
    }

    public Map<String[], Object> toTree() {
        Map<String[], Object> map = new HashMap<>();
        map.put(new String[0], null);
        return map;
    }

    public AbstractElement clone() {
        return this;
    }

    public Type getType() {
        return Type.NULL;
    }

    public boolean equals(Object obj) {
        return obj == null || obj instanceof AbstractNull;
    }
}
