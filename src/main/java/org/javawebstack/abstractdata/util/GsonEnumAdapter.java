package org.javawebstack.abstractdata.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonEnumAdapter implements JsonSerializer<GsonEnum>, JsonDeserializer<GsonEnum> {

    public GsonEnum deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonPrimitive() || !jsonElement.getAsJsonPrimitive().isString())
            return null;
        String value = jsonElement.getAsJsonPrimitive().getAsString();
        if (!(type instanceof Class))
            return null;
        Class<GsonEnum> typeClass = (Class<GsonEnum>) type;
        if (!GsonEnum.class.isAssignableFrom(typeClass))
            return null;
        if (!typeClass.isEnum())
            return null;
        for (GsonEnum e : typeClass.getEnumConstants()) {
            if (e.gsonValue().equals(value))
                return e;
        }
        return null;
    }

    public JsonElement serialize(GsonEnum gsonEnum, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(gsonEnum.gsonValue());
    }

}
