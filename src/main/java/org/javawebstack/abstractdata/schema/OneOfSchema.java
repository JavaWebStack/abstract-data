package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneOfSchema implements AbstractSchema {

    private final List<AbstractSchema> schemas = new ArrayList<>();

    public OneOfSchema(AbstractSchema... schemas) {
        if(schemas.length == 0)
            throw new IllegalArgumentException("At least one schema is required");
        this.schemas.addAll(Arrays.asList(schemas));
    }

    public List<SchemaValidationError> validate(AbstractPath path, AbstractElement value) {
        List<List<SchemaValidationError>> schemaErrors = new ArrayList<>();
        for(AbstractSchema schema : schemas) {
            List<SchemaValidationError> errors = schema.validate(path, value);
            if(errors.isEmpty())
                return errors;
            schemaErrors.add(errors);
        }
        for(List<SchemaValidationError> errors : schemaErrors) {
            if(!(errors.size() == 1 && errors.get(0).getError().equals("invalid_type")))
                return errors;
        }
        return schemaErrors.get(0);
    }

}
