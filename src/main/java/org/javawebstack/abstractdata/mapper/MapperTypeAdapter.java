package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.mapper.exception.MapperException;

public interface MapperTypeAdapter {

    AbstractElement toAbstract(MapperContext context, Object value) throws MapperException;
    Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException;

}
