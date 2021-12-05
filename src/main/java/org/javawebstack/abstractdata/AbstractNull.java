package org.javawebstack.abstractdata;

import java.util.HashMap;
import java.util.Map;

public class AbstractNull implements AbstractElement {

    public static final AbstractNull INSTANCE = new AbstractNull();

    private AbstractNull() {
    }

    public boolean isNull() {
        return true;
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
}
