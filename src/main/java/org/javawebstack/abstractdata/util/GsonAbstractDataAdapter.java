package org.javawebstack.abstractdata.util;

import com.google.gson.*;
import org.javawebstack.abstractdata.AbstractElement;

import java.lang.reflect.Type;

public class GsonAbstractDataAdapter<T extends AbstractElement> implements JsonSerializer<T>, JsonDeserializer<T> {

    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return (T) AbstractElement.fromJson(jsonElement);
    }

    public JsonElement serialize(T t, Type type, JsonSerializationContext jsonSerializationContext) {
        return t.toJson();
    }

}
