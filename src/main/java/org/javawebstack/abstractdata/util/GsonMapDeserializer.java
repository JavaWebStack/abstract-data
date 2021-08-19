package org.javawebstack.abstractdata.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class GsonMapDeserializer<K,V> implements JsonDeserializer<Map<K, V>> {

    protected abstract Class<K> getKeyType();
    protected abstract Class<V> getValueType();

    public Map<K, V> deserialize(JsonElement json, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
        if (json == null || !json.isJsonObject())
            return null;
        JsonObject jsonObject = json.getAsJsonObject();
        Map<K, V> map = new HashMap<>();
        Class<?> t = getValueType();
        for (String k : jsonObject.keySet())
            map.put(deserializationContext.deserialize(new JsonPrimitive(k), getKeyType()), deserializationContext.deserialize(jsonObject.get(k), t));
        return map;
    }
}
