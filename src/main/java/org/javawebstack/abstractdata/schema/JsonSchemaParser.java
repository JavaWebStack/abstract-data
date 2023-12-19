package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;

import java.util.Collections;
import java.util.List;

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
        String schemaStr = "{\n" +
                "    \"$id\": \"1224\",\n" +
                "    \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "    \"title\": \"Person\",\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "        \"name\": {\n" +
                "            \"type\": \"string\"\n" +
                "        },\n" +
                "        \"emails\": {\n" +
                "            \"type\": \"array\"\n" +
                "            \"minItems\": 1\n" +
                "            \"items\": {\n" +
                "                \"type\": \"string\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"age\": {\n" +
                "            \"type\": \"integer\",\n" +
                "            \"minimum\": 0,\n" +
                "            \"maximum\": 120\n" +
                "        }\n" +
                "    },\n" +
                "    \"required\": [\n" +
                "        \"name\",\n" +
                "        \"emails\"\n" +
                "    ]\n" +
                "}";
        AbstractObject schemaObj = AbstractElement.fromJson(schemaStr).object();
        AbstractSchema schema = new JsonSchemaParser().parse(schemaObj);
        List<SchemaValidationError> errors = schema.validate(new AbstractObject()
                .set("name", "Maher")
                .set("emails", new AbstractArray())
                .set("age", 123)
        );
        System.out.println();

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
                case "integer": {
                    return parseInteger(schema);
                }
                default: {
                    throw new UnsupportedOperationException("Unknown type: " + schema.string("type"));
                }
            }
        }
        if(schema.has("$ref")) {
            throw new UnsupportedOperationException("$ref is currently not supported");
        }
        throw new IllegalArgumentException("Invalid json schema");
    }

    private AbstractStringSchema parseString(AbstractObject schema) {
        if(!schema.string("type").equals("string"))
            throw new IllegalArgumentException("Not a valid string schema");
        AbstractStringSchema s = new AbstractStringSchema();

        return s;
    }

    private AbstractNumberSchema parseInteger(AbstractObject schema) {
        if(!schema.string("type").equals("integer"))
            throw new IllegalArgumentException("Not a valid integer schema");
        AbstractNumberSchema s = new AbstractNumberSchema();
        if(schema.has("minimum"))
            s.min(schema.number("minimum"));
        if(schema.has("maximum"))
            s.max(schema.number("maximum"));
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
        return s;
    }

}
