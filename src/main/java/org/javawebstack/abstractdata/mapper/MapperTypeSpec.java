package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.mapper.annotation.Additional;
import org.javawebstack.abstractdata.mapper.annotation.MapperOptions;
import org.javawebstack.abstractdata.mapper.exception.MapperException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class MapperTypeSpec {

    private static final Map<Class<?>, MapperTypeSpec> typeSpecs = new HashMap<>();

    public static MapperTypeSpec get(Class<?> type) throws MapperException {
        if (type.isArray() || type.isEnum() || type.isPrimitive())
            return null;
        if (Integer.class.equals(type))
            return null;
        if (Long.class.equals(type))
            return null;
        if (Double.class.equals(type))
            return null;
        if (Float.class.equals(type))
            return null;
        if (Short.class.equals(type))
            return null;
        if (Boolean.class.equals(type))
            return null;
        return typeSpecs.computeIfAbsent(type, MapperTypeSpec::new);
    }

    public static void reset() {
        typeSpecs.clear();
    }

    private final Class<?> type;
    private final List<FieldSpec> fieldSpecs = new ArrayList<>();
    private Field additionalField;

    public MapperTypeSpec(Class<?> type) throws MapperException {
        this.type = type;
        Stack<Class<?>> classes = new Stack<>();
        Class<?> current = type;
        do {
            classes.push(current);
            current = current.getSuperclass();
        } while (!Object.class.equals(current)); // Collect parent classes recursively
        while (!classes.empty()) {
            for (Field f : classes.pop().getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) // Don't include static fields
                    continue;
                checkoutField(f);
            }
        }
        fieldSpecs.sort(Comparator.comparingInt(a -> a.order));
    }

    private void checkoutField(Field field) throws MapperException {
        Map<Class<? extends Annotation>, List<Annotation>> annotations = new HashMap<>();
        for (Annotation annotation : field.getDeclaredAnnotations())
            annotations.computeIfAbsent(annotation.annotationType(), k -> new ArrayList<>()).add(annotation);

        if (annotations.containsKey(Additional.class)) {
            if (!field.getType().equals(AbstractObject.class))
                throw new MapperException("Additional field '" + field.getName() + "' in type '" + field.getDeclaringClass().getName() + "' needs to be of type AbstractObject, found '" + field.getType().getName() + "'");
            additionalField = field;
            return;
        }

        FieldSpec spec = new FieldSpec();
        fieldSpecs.add(spec);

        spec.field = field;
        spec.annotations = annotations;

        if (annotations.containsKey(MapperOptions.class)) {
            MapperOptions options = (MapperOptions) annotations.get(MapperOptions.class).get(0);
            if (options.name().length() > 0)
                spec.name = options.name();
            if (!options.adapter().equals(MapperTypeAdapter.class)) {
                try {
                    spec.adapter = options.adapter().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new MapperException(e.getMessage());
                }
            }
            spec.order = options.order();
            spec.expose = options.expose();
            spec.hidden = options.hidden();
            spec.omitNull = options.omitNull();
        }
        spec.hidden = spec.hidden || Modifier.isTransient(field.getModifiers());
    }

    public Class<?> getType() {
        return type;
    }

    public List<FieldSpec> getFieldSpecs() {
        return fieldSpecs;
    }

    public Field getAdditionalField() {
        if (additionalField != null)
            additionalField.setAccessible(true);
        return additionalField;
    }

    public static class FieldSpec {

        private Field field;
        private String name;
        private int order;
        private boolean expose;
        private boolean hidden;
        private boolean omitNull = true;
        private MapperTypeAdapter adapter;
        private Map<Class<? extends Annotation>, List<Annotation>> annotations = new HashMap<>();

        public Field getField() {
            field.setAccessible(true);
            return field;
        }

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }

        public boolean isExpose() {
            return expose;
        }

        public boolean isHidden() {
            return hidden;
        }

        public boolean shouldOmitNull() {
            return omitNull;
        }

        public MapperTypeAdapter getAdapter() {
            return adapter;
        }

        public Map<Class<? extends Annotation>, List<Annotation>> getAnnotations() {
            return annotations;
        }

    }

}
