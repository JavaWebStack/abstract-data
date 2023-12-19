package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.AbstractPath;
import org.javawebstack.abstractdata.AbstractPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbstractBooleanSchema implements AbstractSchema {

    private Boolean staticValue;
    private final List<CustomValidation<AbstractPrimitive>> customValidations = new ArrayList<>();

    public AbstractBooleanSchema staticValue(boolean value) {
        this.staticValue = value;
        return this;
    }

    public AbstractBooleanSchema customValidation(CustomValidation<AbstractPrimitive> validation) {
        customValidations.add(validation);
        return this;
    }

    public Boolean getStaticValue() {
        return staticValue;
    }

    public List<CustomValidation<AbstractPrimitive>> getCustomValidations() {
        return customValidations;
    }

    @Override
    public AbstractObject toJsonSchema() {
        AbstractObject obj = new AbstractObject()
                .set("type", "boolean");
        if (staticValue != null) {
            obj.set("const", staticValue);
        }
        return obj;
    }

    public List<SchemaValidationError> validate(AbstractPath path, AbstractElement value) {
        List<SchemaValidationError> errors = new ArrayList<>();
        if (value.getType() != AbstractElement.Type.BOOLEAN) {
            errors.add(new SchemaValidationError(path, "invalid_type").meta("expected", "boolean").meta("actual", value.getType().name().toLowerCase(Locale.ROOT)));
            return errors;
        }
        if (staticValue != null && staticValue != value.bool()) {
            errors.add(new SchemaValidationError(path, "invalid_static_value").meta("expected", staticValue.toString()).meta("actual", value.bool().toString()));
        }
        for (CustomValidation<AbstractPrimitive> validation : customValidations) {
            errors.addAll(validation.validate(path, value.primitive()));
        }
        return errors;
    }

}
