package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.mapper.annotation.MapperOptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapperContext {

    private final Mapper mapper;
    private final Field field;
    private MapperTypeAdapter adapter;
    private final Map<Class<? extends Annotation>, List<Annotation>> annotations;

    public MapperContext(Mapper mapper, Field field, Map<Class<? extends Annotation>, List<Annotation>> annotations) {
        this.mapper = mapper;
        this.field = field;
        this.annotations = annotations;
    }

    public MapperContext adapter(MapperTypeAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return field != null ? field.getName() : null;
    }

    public boolean isNested() {
        return field != null;
    }

    public Map<Class<? extends Annotation>, List<Annotation>> getAnnotations() {
        return annotations;
    }

    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        if (!annotations.containsKey(type))
            return new ArrayList<>();
        return (List<T>) annotations.get(type);
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        if (!annotations.containsKey(type))
            return null;
        return (T) annotations.get(type).get(0);
    }

    public Class<?>[] getGenericTypes() {
        MapperOptions options = getAnnotation(MapperOptions.class);
        return options == null ? new Class[0] : options.generic();
    }

    public MapperTypeAdapter getAdapter() {
        return adapter;
    }
}
