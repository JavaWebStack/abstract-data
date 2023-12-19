package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;

import java.util.*;

/*
{
    "$id": "1224",
    "$schema": "https://json-schema.org/draft/2020-12/schema",
    "title": "Person",
    "type": "object",
    "properties": {
        "name": {
            "type": "string"
        },
        "emails": {
            "type": "array"
            "items": {
                "type": "string"
            }
        },
        "age": {
            "type": "integer",
            "minimum": 0,
            "maximum": 120
        }
    },
    "required": [
        "name",
        "emails"
    ]
}
 */

public class JsonSchemaParser {

    public static void main(String[] args) {

    }

    public AbstractSchema parse(AbstractObject schema) {
        if(schema.has("type")) {
            switch (schema.string("type")) {
                case "object": {
                    return parseObject(schema);
                }
                case "array": {
                    return parseArray(schema);
                }
                case "string": {
                    return parseString(schema);
                }
                case "number":
                case "integer": {
                    return parseNumber(schema);
                }
                case "boolean": {
                    return parseBoolean(schema);
                }
                default: {
                    throw new UnsupportedOperationException("Unknown type: " + schema.string("type"));
                }
            }
        }
        if(schema.has("$ref")) {
            throw new UnsupportedOperationException("$ref is currently not supported");
        }
        if(schema.has("oneOf")){
            return parseOneOf(schema);
        }
        throw new IllegalArgumentException("Invalid json schema");
    }

    private OneOfSchema parseOneOf(AbstractObject schema) {
        if(!schema.hasArray("oneOf")){
            throw new IllegalArgumentException("Not a valid oneOf schema");
        }
        AbstractArray absArr = schema.array("oneOf");
        AbstractSchema[] schemas = new AbstractSchema[absArr.size()];
        for(int i = 0; i<schemas.length;i++){
            schemas[i] = parse(absArr.object(i));
        }
        return new OneOfSchema(schemas);
    }
    private AbstractStringSchema parseString(AbstractObject schema) {
        if(!schema.string("type").equals("string"))
            throw new IllegalArgumentException("Not a valid string schema");
        AbstractStringSchema s = new AbstractStringSchema();

        if(schema.has("minLength")){
            s.minLength(schema.number("minLength").intValue());
        }
        if(schema.has("maxLength")){
            s.maxLength(schema.number("maxLength").intValue());
        }
        if(schema.has("pattern")) {
            s.regex(schema.string("pattern"));
        }
        if(schema.has("enum")){
            s.enumValues(new HashSet<>(schema.array("enum").toStringList()));
        }
        if(schema.has("const")){
            s.staticValue(schema.string("const"));
        }
        return s;
    }

    private AbstractNumberSchema parseNumber(AbstractObject schema) {
        boolean isInteger = schema.string("type").equals("integer");

        if (!isInteger && !schema.string("type").equals("number")) {
            throw new IllegalArgumentException("Not a valid number schema");
        }
        AbstractNumberSchema s = new AbstractNumberSchema();
        if(isInteger){
            s.integerOnly();
        }
        if(schema.has("minimum"))
            s.min(schema.number("minimum"));
        if(schema.has("maximum"))
            s.max(schema.number("maximum"));
        if(schema.has("exclusiveMinimum"))
            s.min(schema.number("exclusiveMinimum"),true);
        if(schema.has("exclusiveMaximum"))
            s.max(schema.number("exclusiveMaximum"),true);
        if(schema.has("const")){
            Number staticValue = schema.number("const");
            s.min(staticValue);
            s.max(staticValue);
        }
        if(schema.has("multipleOf")){
            s.step(schema.number("multipleOf"));
        }

        return s;
    }


    private AbstractObjectSchema parseObject(AbstractObject schema) {
        if(!schema.string("type").equals("object"))
            throw new IllegalArgumentException("Not a valid object schema");
        AbstractObjectSchema s = new AbstractObjectSchema();
        List<String> required = schema.has("required") ? schema.array("required").toStringList() : Collections.emptyList();
        if(schema.has("properties")) {
            schema.object("properties").forEach((key, propSchema) -> {
                if(required.contains(key)) {
                    s.requiredProperty(key, parse(propSchema.object()));
                } else {
                    s.optionalProperty(key, parse(propSchema.object()));
                }
            });
        }
        AbstractElement additionalProperties = schema.get("additionalProperties");
        if(additionalProperties != null){
            if(additionalProperties.isBoolean()){
                if(additionalProperties.bool()){
                    s.additionalProperties();
                }
            } else {
                s.additionalProperties(parse(additionalProperties.object()));
            }


        } else {
            s.additionalProperties();
        }
        return s;
    }

    private AbstractArraySchema parseArray(AbstractObject schema) {
        if(!schema.string("type").equals("array"))
            throw new IllegalArgumentException("Not a valid array schema");
        AbstractArraySchema s = new AbstractArraySchema();
        if(schema.has("items")) {
            s.itemSchema(parse(schema.object("items")));
        }
        if(schema.has("minItems")) {
            s.min(schema.number("minItems").intValue());
        }
        if(schema.has("maxItems")) {
            s.max(schema.number("maxItems").intValue());
        }
        if(schema.has("uniqueItems") && schema.bool("uniqueItems")){
            s.unique();
        }
        return s;
    }

    private AbstractBooleanSchema parseBoolean(AbstractObject schema){
        if(!schema.string("type").equals("boolean"))
            throw new IllegalArgumentException("Not a valid boolean schema");
        AbstractBooleanSchema s = new AbstractBooleanSchema();
        if(schema.has("const")){
            s.staticValue(schema.bool("const"));
        }


        return new AbstractBooleanSchema();

    }

}
