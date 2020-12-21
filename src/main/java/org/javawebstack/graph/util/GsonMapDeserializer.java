package org.javawebstack.graph.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class GsonMapDeserializer<T> implements JsonDeserializer<Map<String,T>> {
    protected abstract Class<T> getType();
    public Map<String, T> deserialize(JsonElement json, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
        if(json == null || !json.isJsonObject())
            return null;
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, T> map = new HashMap<>();
        Class<?> t = getType();
        for(String k : jsonObject.keySet())
            map.put(k, deserializationContext.deserialize(jsonObject.get(k), t));
        return map;
    }
}
