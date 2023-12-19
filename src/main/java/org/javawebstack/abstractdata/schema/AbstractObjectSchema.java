package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.AbstractPath;

import java.util.*;

public class AbstractObjectSchema implements AbstractSchema {

    private final Map<String, AbstractSchema> properties = new HashMap<>();
    private final Set<String> requiredProperties = new HashSet<>();
    private final List<CustomValidation<AbstractObject>> customValidations = new ArrayList<>();
    private boolean allowAdditionalProperties = false;
    private AbstractSchema additionalPropertySchema;

    public AbstractObjectSchema requiredProperty(String name, AbstractSchema schema) {
        properties.put(name, schema);
        requiredProperties.add(name);
        return this;
    }

    public AbstractObjectSchema optionalProperty(String name, AbstractSchema schema) {
        properties.put(name, schema);
        requiredProperties.remove(name);
        return this;
    }

    public AbstractObjectSchema customValidation(CustomValidation<AbstractObject> validation) {
        customValidations.add(validation);
        return this;
    }

    public AbstractObjectSchema additionalProperties() {
        return additionalProperties(null);
    }

    public AbstractObjectSchema additionalProperties(AbstractSchema schema) {
        allowAdditionalProperties = true;
        additionalPropertySchema = schema;
        return this;
    }

    @Override
    public AbstractObject toJsonSchema() {
        AbstractObject obj = new AbstractObject();
        obj.set("type", "object");
        AbstractObject properties = new AbstractObject();
        this.properties.forEach((key, value) -> {
            properties.set(key, value.toJsonSchema());
        });
        obj.set("properties", properties);

        if (!requiredProperties.isEmpty()) {
            AbstractArray required = new AbstractArray();
            requiredProperties.forEach(required::add);
            obj.set("required", required);
        }
        if (!allowAdditionalProperties) {
            obj.set("additionalProperties", false);
        } else if (additionalPropertySchema != null) {
            obj.set("additionalProperties", additionalPropertySchema.toJsonSchema());
        }

        return obj;
    }

    public List<SchemaValidationError> validate(AbstractPath path, AbstractElement value) {
        List<SchemaValidationError> errors = new ArrayList<>();
        if (value.getType() != AbstractElement.Type.OBJECT) {
            errors.add(new SchemaValidationError(path, "invalid_type").meta("expected", "object").meta("actual", value.getType().name().toLowerCase(Locale.ROOT)));
            return errors;
        }
        AbstractObject object = value.object();
        for (String prop : requiredProperties) {
            if (!object.has(prop) || object.get(prop).isNull()) {
                errors.add(new SchemaValidationError(path.subPath(prop), "missing_required_property"));
            }
        }
        for (String prop : object.keys()) {
            AbstractElement propValue = object.get(prop);
            AbstractPath propPath = path.subPath(prop);
            if (properties.containsKey(prop)) {
                if (propValue.isNull())
                    continue;
                errors.addAll(properties.get(prop).validate(propPath, propValue));
            } else {
                if (allowAdditionalProperties) {
                    if (additionalPropertySchema != null) {
                        errors.addAll(additionalPropertySchema.validate(propPath, propValue));
                    }
                } else {
                    errors.add(new SchemaValidationError(propPath, "unexpected_property"));
                }
            }
        }
        for (CustomValidation<AbstractObject> validation : customValidations) {
            errors.addAll(validation.validate(path, object));
        }
        return errors;
    }

}
