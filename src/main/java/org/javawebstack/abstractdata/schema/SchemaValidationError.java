package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaValidationError {

    private static final Map<String, String> BUILTIN_DESCRIPTIONS = new HashMap<String, String>() {{
        put("invalid_type", "Expected value of type {expected}, got {actual}");
        put("not_enough_items", "At least {min} item(s) required, got {actual}");
        put("too_many_items", "Not more than {max} item(s) allowed, got {actual}");
        put("null_not_allowed", "Value null is not allowed");
        put("invalid_static_value", "Static value does not match, expected '{expected}' but got '{actual}'");
        put("number_smaller_than_min", "The value '{actual}' is smaller than {min}");
        put("number_larger_than_max", "The value '{actual}' is larger than {max}");
        put("missing_required_property", "The property is required but missing");
        put("unexpected_property", "Unexpected property, additional properties are not allowed");
        put("value_too_short", "The length of the value ({actual}) is shorter than the minimum of {min}");
        put("value_too_long", "The length of the value ({actual}) is longer than the maximum of {max}");
        put("invalid_pattern", "The value '{actual}' does not match the pattern '{pattern}'");
        put("number_not_within_step", "The value '{start}' was not in steps of {step} starting from {start}");
        put("duplicate_array_value", "The value '{value}' is a duplicate of '{first}'");
    }};

    private AbstractPath path;
    private String error;
    private Map<String, String> errorMeta = new HashMap<>();

    public SchemaValidationError(AbstractPath path, String error) {
        this.path = path;
        this.error = error;
    }

    public SchemaValidationError meta(String key, String value) {
        errorMeta.put(key, value);
        return this;
    }

    public AbstractPath getPath() {
        return path;
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getErrorMeta() {
        return errorMeta;
    }

    public String getErrorDescription() {
        return getErrorDescription(new HashMap<>());
    }

    public String getErrorDescription(Map<String, String> customDescriptions) {
        String message;
        if (customDescriptions.containsKey(error)) {
            message = customDescriptions.get(error);
        } else if (BUILTIN_DESCRIPTIONS.containsKey(error)) {
            message = BUILTIN_DESCRIPTIONS.get(error);
        } else {
            return error;
        }
        for (String key : errorMeta.keySet()) {
            message = message.replace("{" + key + "}", errorMeta.get(key));
        }
        return message;
    }

    public static Map<AbstractPath, List<SchemaValidationError>> groupErrors(List<SchemaValidationError> errors) {
        Map<AbstractPath, List<SchemaValidationError>> errorMap = new HashMap<>();
        for (SchemaValidationError e : errors) {
            errorMap.computeIfAbsent(e.getPath(), k -> new ArrayList<>()).add(e);
        }
        return errorMap;
    }

}
