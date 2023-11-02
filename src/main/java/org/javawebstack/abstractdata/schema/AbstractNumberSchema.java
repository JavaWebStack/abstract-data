package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractPath;
import org.javawebstack.abstractdata.AbstractPrimitive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbstractNumberSchema implements AbstractSchema {

    private boolean integerOnly = false;
    private Number min;
    private Number max;
    private final List<CustomValidation<AbstractPrimitive>> customValidations = new ArrayList<>();

    public AbstractNumberSchema min(Number min) {
        this.min = min;
        return this;
    }

    public AbstractNumberSchema max(Number max) {
        this.max = max;
        return this;
    }

    public AbstractNumberSchema integerOnly() {
        this.integerOnly = true;
        return this;
    }

    public AbstractNumberSchema customValidation(CustomValidation<AbstractPrimitive> validation) {
        customValidations.add(validation);
        return this;
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public boolean isIntegerOnly() {
        return integerOnly;
    }

    public List<CustomValidation<AbstractPrimitive>> getCustomValidations() {
        return customValidations;
    }

    public List<SchemaValidationError> validate(AbstractPath path, AbstractElement value) {
        List<SchemaValidationError> errors = new ArrayList<>();
        if(value.getType() != AbstractElement.Type.NUMBER) {
            errors.add(new SchemaValidationError(path, "invalid_type").meta("expected", integerOnly ? "integer" : "number").meta("actual", value.getType().name().toLowerCase(Locale.ROOT)));
            return errors;
        }
        Number n = value.number();
        BigDecimal dN = (n instanceof Float || n instanceof Double) ? BigDecimal.valueOf(n.doubleValue()) : BigDecimal.valueOf(n.longValue());
        if(integerOnly && (n instanceof Float || n instanceof Double)) {
            errors.add(new SchemaValidationError(path, "invalid_type").meta("expected", "integer").meta("actual", "number"));
            return errors;
        }
        if(min != null) {
            BigDecimal dMin = (min instanceof Float || min instanceof Double) ? BigDecimal.valueOf(min.doubleValue()) : BigDecimal.valueOf(min.longValue());
            if(dN.compareTo(dMin) < 0) {
                errors.add(new SchemaValidationError(path, "number_smaller_than_min").meta("min", dMin.toPlainString()).meta("actual", dN.toPlainString()));
            }
        }
        if(max != null) {
            BigDecimal dMax = (max instanceof Float || min instanceof Double) ? BigDecimal.valueOf(max.doubleValue()) : BigDecimal.valueOf(max.longValue());
            if(dN.compareTo(dMax) > 0) {
                errors.add(new SchemaValidationError(path, "number_larger_than_max").meta("max", dMax.toPlainString()).meta("actual", dN.toPlainString()));
            }
        }
        for(CustomValidation<AbstractPrimitive> validation : customValidations) {
            errors.addAll(validation.validate(path, value.primitive()));
        }
        return errors;
    }

}
