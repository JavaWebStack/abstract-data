package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractStringSchema implements AbstractSchema {

    private Integer minLength;
    private Integer maxLength;
    private String staticValue;
    private String regex;
    private Pattern regexPattern;
    private Set<String> enumValues;
    private final List<CustomValidation<AbstractPrimitive>> customValidations = new ArrayList<>();

    public AbstractStringSchema staticValue(String value) {
        this.staticValue = value;
        return this;
    }

    public AbstractStringSchema enumValues(Class<? extends Enum<?>> enumType) {
        Set<String> values = new HashSet<>();
        for(Enum<?> v : enumType.getEnumConstants()) {
            values.add(v.name());
        }
        return enumValues(values);
    }

    public AbstractStringSchema enumValues(String... values) {
        return enumValues(new HashSet<>(Arrays.asList(values)));
    }

    public AbstractStringSchema enumValues(Set<String> values) {
        this.enumValues = values;
        return this;
    }

    public AbstractStringSchema minLength(int min) {
        this.minLength = min;
        return this;
    }

    public AbstractStringSchema maxLength(int max) {
        this.maxLength = max;
        return this;
    }

    public AbstractStringSchema regex(String regex) {
        this.regex = regex;
        this.regexPattern = Pattern.compile(regex);
        return this;
    }

    public AbstractStringSchema customValidation(CustomValidation<AbstractPrimitive> validation) {
        customValidations.add(validation);
        return this;
    }

    public String getRegex() {
        return regex;
    }

    public String getStaticValue() {
        return staticValue;
    }

    public List<CustomValidation<AbstractPrimitive>> getCustomValidations() {
        return customValidations;
    }

    @Override
    public AbstractObject toJsonSchema() {
        AbstractObject obj = new AbstractObject();
        obj.set("type","string");
        if(minLength != null){
            obj.set("minLength",minLength);
        }
        if(maxLength != null){
            obj.set("maxLength",maxLength);
        }
        if(staticValue != null) {
            obj.set("const",staticValue);
        }
        if(regex != null) {
            obj.set("pattern",regex);
        }
        if(enumValues != null) {
            AbstractArray arr = new AbstractArray(enumValues.toArray());
            obj.set("enum",arr);
        }

        return obj;
    }

    public List<SchemaValidationError> validate(AbstractPath path, AbstractElement value) {
        List<SchemaValidationError> errors = new ArrayList<>();
        if(value.getType() != AbstractElement.Type.STRING) {
            errors.add(new SchemaValidationError(path, "invalid_type").meta("expected", "string").meta("actual", value.getType().name().toLowerCase(Locale.ROOT)));
            return errors;
        }
        String s = value.string();
        if(staticValue != null && !staticValue.equals(s)) {
            errors.add(new SchemaValidationError(path, "invalid_static_value").meta("expected", staticValue).meta("actual", s));
        }
        if(enumValues != null && !enumValues.contains(s)) {
            errors.add(new SchemaValidationError(path, "invalid_enum_value").meta("expected", String.join(", ", enumValues)).meta("actual", s));
        }
        if(minLength != null && s.length() < minLength) {
            errors.add(new SchemaValidationError(path, "value_too_short").meta("min", minLength.toString()).meta("actual", String.valueOf(s.length())));
        }
        if(maxLength != null && s.length() > maxLength) {
            errors.add(new SchemaValidationError(path, "value_too_long").meta("max", maxLength.toString()).meta("actual", String.valueOf(s.length())));
        }
        if(regexPattern != null) {
            Matcher matcher = regexPattern.matcher(s);
            if(!matcher.matches()) {
                errors.add(new SchemaValidationError(path, "invalid_pattern").meta("pattern", regex).meta("actual", s));
            }
        }
        for(CustomValidation<AbstractPrimitive> validation : customValidations) {
            errors.addAll(validation.validate(path, value.primitive()));
        }
        return errors;
    }

}
