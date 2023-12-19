package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.AbstractPath;

import java.util.List;

public interface AbstractSchema {
    AbstractObject toJsonSchema();

    default List<SchemaValidationError> validate(AbstractElement value) {
        return validate(AbstractPath.ROOT, value);
    }

    List<SchemaValidationError> validate(AbstractPath path, AbstractElement value);

    static AbstractArraySchema array(AbstractSchema itemSchema) {
        return array().itemSchema(itemSchema);
    }

    static AbstractArraySchema array() {
        return new AbstractArraySchema();
    }

    static AbstractObjectSchema object() {
        return new AbstractObjectSchema();
    }

    static AbstractStringSchema staticString(String s) {
        return string().staticValue(s);
    }

    static AbstractStringSchema enumString(Class<? extends Enum<?>> enumType) {
        return string().enumValues(enumType);
    }

    static AbstractStringSchema enumString(String... values) {
        return string().enumValues(values);
    }

    static AbstractStringSchema string() {
        return new AbstractStringSchema();
    }

    static AbstractNumberSchema integer(int min, int max) {
        return integer().min(min).max(max);
    }

    static AbstractNumberSchema integer() {
        return new AbstractNumberSchema().integerOnly();
    }

    static AbstractNumberSchema number() {
        return new AbstractNumberSchema();
    }

    static AbstractNumberSchema number(double min, double max) {
        return number().min(min).max(max);
    }

    static AbstractBooleanSchema staticBool(boolean v) {
        return bool().staticValue(v);
    }

    static AbstractBooleanSchema bool() {
        return new AbstractBooleanSchema();
    }

    static OneOfSchema oneOf(AbstractSchema... schemas) {
        return new OneOfSchema(schemas);
    }

    static AbstractSchema fromJsonSchema(AbstractObject schema) {
        return new JsonSchemaParser().parse(schema);
    }

}
