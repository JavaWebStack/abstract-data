package org.javawebstack.graph;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.HashMap;
import java.util.Map;

public class GraphNull implements GraphElement {

    public static final GraphNull INSTANCE = new GraphNull();

    private GraphNull(){}

    public boolean isNull() {
        return true;
    }

    public JsonElement toJson() {
        return JsonNull.INSTANCE;
    }

    public Map<String[], Object> toTree() {
        Map<String[], Object> map = new HashMap<>();
        map.put(new String[0], null);
        return map;
    }

    public Type getType() {
        return Type.NULL;
    }
}
