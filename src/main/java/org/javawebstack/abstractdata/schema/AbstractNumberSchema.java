package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
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
    private boolean minExclusive = false;
    private boolean maxExclusive = false;
    private Number step;
    private final List<CustomValidation<AbstractPrimitive>> customValidations = new ArrayList<>();

    public AbstractNumberSchema min(Number min) {
        return min(min,false);
    }

    public AbstractNumberSchema max(Number max) {
        return max(max,false);
    }

    public AbstractNumberSchema min(Number min, boolean exclusive) {
        this.minExclusive = exclusive;
        this.min = min;
        return this;
    }

    public AbstractNumberSchema max(Number max, boolean exclusive) {
        this.maxExclusive = exclusive;
        this.max = max;
        return this;
    }

    public AbstractNumberSchema step(Number step){
        this.step = step;
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

    @Override
    public AbstractObject toJsonSchema() {
        AbstractObject obj = new AbstractObject();

        obj.set("type",integerOnly ? "integer" : "number");
        if(min != null && max != null && !minExclusive && !maxExclusive){
            BigDecimal dMin = (min instanceof Float || min instanceof Double) ? BigDecimal.valueOf(min.doubleValue()) : BigDecimal.valueOf(min.longValue());
            BigDecimal dMax = (max instanceof Float || max instanceof Double) ? BigDecimal.valueOf(max.doubleValue()) : BigDecimal.valueOf(max.longValue());

            if(dMin.compareTo(dMax)==0){
                obj.set("const",min);
            }

        }
        if(!obj.has("const")) {
            if (min != null) {
                obj.set(minExclusive ? "exclusiveMinimum" : "minimum", min);
            }
            if (max != null) {
                obj.set(maxExclusive ? "exclusiveMaximum" : "maximum", max);
            }
            if (step != null) {
                obj.set("multipleOf", step);
            }
        }


        return obj;
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
            if(!(dN.compareTo(dMin) > (minExclusive ? 0 : -1))) {
                errors.add(new SchemaValidationError(path, "number_smaller_than_min").meta("min", dMin.toPlainString()).meta("actual", dN.toPlainString()));
            }
        }
        if(max != null) {
            BigDecimal dMax = (max instanceof Float || max instanceof Double) ? BigDecimal.valueOf(max.doubleValue()) : BigDecimal.valueOf(max.longValue());
            if(!(dN.compareTo(dMax) < (maxExclusive ? 0 : 1))) {
                errors.add(new SchemaValidationError(path, "number_larger_than_max").meta("max", dMax.toPlainString()).meta("actual", dN.toPlainString()));
            }
        }
        if(step != null) {
            if(min != null && minExclusive){
                throw new UnsupportedOperationException("Step is not supported together with an exclusive minimum");
            }
            BigDecimal dMin = min == null ? BigDecimal.ZERO : (min instanceof Float || min instanceof Double) ? BigDecimal.valueOf(min.doubleValue()) : BigDecimal.valueOf(min.longValue());
            BigDecimal dStep =  (step instanceof Float || step instanceof Double) ? BigDecimal.valueOf(step.doubleValue()) : BigDecimal.valueOf(step.longValue());

            if(dN.subtract(dMin).remainder(dStep).compareTo(BigDecimal.ZERO) != 0) {
                errors.add(new SchemaValidationError(path, "number_not_within_step").meta("step", dStep.toPlainString()).meta("actual", dN.toPlainString()).meta("start",dMin.toPlainString()));
            }
        }
        for(CustomValidation<AbstractPrimitive> validation : customValidations) {
            errors.addAll(validation.validate(path, value.primitive()));
        }
        return errors;
    }

}
