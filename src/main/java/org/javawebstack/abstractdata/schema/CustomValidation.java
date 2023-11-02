package org.javawebstack.abstractdata.schema;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractPath;

import java.util.List;

public interface CustomValidation<T extends AbstractElement> {

    List<SchemaValidationError> validate(AbstractPath path, T value);

}
