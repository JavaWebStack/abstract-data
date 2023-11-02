package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbstractArraySchema implements AbstractSchema {

    private AbstractSchema itemSchema;
    private Integer min;
    private Integer max;
    private boolean allowNull = false;
    private final List<CustomValidation<AbstractArray>> customValidations = new ArrayList<>();

    public AbstractArraySchema itemSchema(AbstractSchema schema) {
        this.itemSchema = schema;
        return this;
    }

    public AbstractArraySchema min(int min) {
        this.min = min;
        return this;
    }

    public AbstractArraySchema max(int max) {
        this.max = max;
        return this;
    }

    public AbstractArraySchema allowNull() {
        this.allowNull = true;
        return this;
    }

    public AbstractArraySchema customValidation(CustomValidation<AbstractArray> validation) {
        customValidations.add(validation);
        return this;
    }

    public AbstractSchema getItemSchema() {
        return itemSchema;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public List<CustomValidation<AbstractArray>> getCustomValidations() {
        return customValidations;
    }

    public List<SchemaValidationError> validate(AbstractPath path, AbstractElement value) {
        List<SchemaValidationError> errors = new ArrayList<>();
        if(value.getType() != AbstractElement.Type.ARRAY) {
            errors.add(new SchemaValidationError(path, "invalid_type").meta("expected", "array").meta("actual", value.getType().name().toLowerCase(Locale.ROOT)));
            return errors;
        }
        AbstractArray array = value.array();
        if(min != null && array.size() < min) {
            errors.add(new SchemaValidationError(path, "not_enough_items").meta("min", String.valueOf(min)).meta("actual", String.valueOf(array.size())));
        }
        if(max != null && array.size() > max) {
            errors.add(new SchemaValidationError(path, "too_many_items").meta("max", String.valueOf(max)).meta("actual", String.valueOf(array.size())));
        }
        if(itemSchema != null) {
            for(int i=0; i<array.size(); i++) {
                AbstractElement item = array.get(i);
                AbstractPath itemPath = path.subPath(String.valueOf(i));
                if(item.isNull()) {
                    if(!allowNull) {
                        errors.add(new SchemaValidationError(itemPath, "null_not_allowed"));
                    }
                    continue;
                }
                errors.addAll(itemSchema.validate(itemPath, array.get(i)));
            }
        }
        for(CustomValidation<AbstractArray> validation : customValidations) {
            errors.addAll(validation.validate(path, array));
        }
        return errors;
    }

}
