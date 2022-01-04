package org.javawebstack.abstractdata.mapper.exception;

public class MapperWrongTypeException extends MapperException {

    public MapperWrongTypeException(String field, String expected, String received) {
        super("Received wrong type" + (field != null ? (" for field '" + field + "'") : "") + ", expected " + expected + " but received " + received);
    }

}
