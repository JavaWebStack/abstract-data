package org.javawebstack.abstractdata.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class GsonListDeserializer<T> implements JsonDeserializer<List<T>> {
    protected abstract Class<T> getType();
    public List<T> deserialize(JsonElement json, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
        if(json == null || !json.isJsonArray())
            return null;
        JsonArray jsonArray = json.getAsJsonArray();
        List<T> list = new ArrayList<>();
        Class<?> t = getType();
        for(JsonElement e : jsonArray)
            list.add(deserializationContext.deserialize(e, t));
        return list;
    }
}
