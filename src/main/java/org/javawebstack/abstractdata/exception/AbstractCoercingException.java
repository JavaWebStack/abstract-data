package org.javawebstack.abstractdata.exception;

import org.javawebstack.abstractdata.AbstractElement;

public class AbstractCoercingException extends RuntimeException {

    public AbstractCoercingException(AbstractElement.Type requested, AbstractElement.Type found) {
        this(requested.name(), found);
    }

    public AbstractCoercingException(String requested, AbstractElement.Type found) {
        super("Type '" + found.name() + "' can not be coerced into type '" + requested + "' or strict mode prohibits type coercing");
    }

    public AbstractCoercingException(AbstractElement.Type requested, AbstractElement found) {
        super("Value '" + found.toJsonString() + "' can not be coerced into type '" + requested.name() + "'");
    }

}
