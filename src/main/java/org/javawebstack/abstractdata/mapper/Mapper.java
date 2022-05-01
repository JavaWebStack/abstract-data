package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractNull;
import org.javawebstack.abstractdata.mapper.exception.MapperException;
import org.javawebstack.abstractdata.mapper.exception.MapperWrongTypeException;
import org.javawebstack.abstractdata.mapper.naming.NamingPolicy;
import org.javawebstack.abstractdata.util.Helpers;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Mapper {

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private NamingPolicy namingPolicy = NamingPolicy.NONE;
    private boolean exposeRequired;
    private boolean omitNull = true;
    private boolean strict = false;
    private final Map<Class<?>, MapperTypeAdapter> adapters = DefaultMappers.create();
    private final MapperContext emptyContext = new MapperContext(this, null, new HashMap<>());

    public <T> T map(AbstractElement element, Class<T> type) throws MapperException {
        return map(emptyContext, element, type);
    }

    public <T> T map(MapperContext context, AbstractElement element, Class<T> type) throws MapperException {
        if(type == null || element == null || element.isNull())
            return null;
        if(type.isArray()) {
            if(!element.isArray())
                throw new MapperWrongTypeException(null, "array", Helpers.typeName(element));
            Object arr = Array.newInstance(type.getComponentType(), element.array().size());
            for(int i=0; i<element.array().size(); i++)
                Array.set(arr, i, map(emptyContext, element.array().get(i), type.getComponentType()));
            return (T) arr;
        }
        if(context.getAdapter() != null)
            return (T) context.getAdapter().fromAbstract(context, element, type);
        return (T) adapters.getOrDefault(type, DefaultMappers.FALLBACK).fromAbstract(context, element, type);
    }

    public AbstractElement map(Object obj) throws MapperException {
        return map(emptyContext, obj);
    }

    public AbstractElement map(MapperContext context, Object obj) throws MapperException {
        if(obj == null)
            return AbstractNull.VALUE;
        if(obj.getClass().isArray()) {
            AbstractArray array = new AbstractArray();
            for(int i=0; i<Array.getLength(obj); i++)
                array.add(map(Array.get(obj, i)));
            return array;
        }
        if(context.getAdapter() != null)
            return context.getAdapter().toAbstract(context, obj);
        return adapters.getOrDefault(obj.getClass(), DefaultMappers.FALLBACK).toAbstract(context, obj);
    }

    public Mapper strict() {
        return strict(true);
    }

    public Mapper strict(boolean strict) {
        this.strict = strict;
        return this;
    }

    public boolean isStrict() {
        return strict;
    }

    public Mapper dateFormat(String format) {
        this.dateFormat = format;
        return this;
    }

    public DateFormat getDateFormat() {
        return new SimpleDateFormat(dateFormat);
    }

    public boolean shouldOmitNull() {
        return omitNull;
    }

    public Mapper omitNull(boolean omitNull) {
        this.omitNull = omitNull;
        return this;
    }

    public Mapper namingPolicy(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
        return this;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    public Mapper requireExpose() {
        return requireExpose(true);
    }

    public Mapper requireExpose(boolean exposeRequired) {
        this.exposeRequired = exposeRequired;
        return this;
    }

    public boolean isExposeRequired() {
        return exposeRequired;
    }

    public Mapper adapter(Class<?> type, MapperTypeAdapter adapter) {
        adapters.put(type, adapter);
        return this;
    }

    public Mapper adapter(MapperTypeAdapter adapter) {
        Class<?>[] types = adapter.getSupportedTypes();
        if(types != null) {
            for(Class<?> type : types)
                adapter(type, adapter);
        }
        return this;
    }

    public Map<Class<?>, MapperTypeAdapter> getAdapters() {
        return adapters;
    }

}
