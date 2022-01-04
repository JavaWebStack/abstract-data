package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.*;
import org.javawebstack.abstractdata.mapper.exception.MapperException;
import org.javawebstack.abstractdata.mapper.exception.MapperWrongTypeException;
import org.javawebstack.abstractdata.util.Helpers;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public final class DefaultMappers {

    public static final PrimitiveMapper PRIMITIVE = new PrimitiveMapper();
    public static final CollectionMapper COLLECTION = new CollectionMapper();
    public static final MapMapper MAP = new MapMapper();
    public static final DateMapper DATE = new DateMapper();
    public static final AbstractMapper ABSTRACT = new AbstractMapper();

    public static final FallbackMapper FALLBACK = new FallbackMapper();

    public static Map<Class<?>, MapperTypeAdapter> create() {
        Map<Class<?>, MapperTypeAdapter> map = new HashMap<>();

        // Abstract
        map.put(AbstractElement.class, ABSTRACT);
        map.put(AbstractNull.class, ABSTRACT);
        map.put(AbstractPrimitive.class, ABSTRACT);
        map.put(AbstractObject.class, ABSTRACT);
        map.put(AbstractArray.class, ABSTRACT);

        // Primitives
        map.put(String.class, PRIMITIVE);
        map.put(Integer.class, PRIMITIVE);
        map.put(int.class, PRIMITIVE);
        map.put(Long.class, PRIMITIVE);
        map.put(long.class, PRIMITIVE);
        map.put(Short.class, PRIMITIVE);
        map.put(short.class, PRIMITIVE);
        map.put(Float.class, PRIMITIVE);
        map.put(float.class, PRIMITIVE);
        map.put(Double.class, PRIMITIVE);
        map.put(double.class, PRIMITIVE);
        map.put(Boolean.class, PRIMITIVE);
        map.put(boolean.class, PRIMITIVE);
        map.put(Number.class, PRIMITIVE);
        map.put(Map.class, MAP);
        map.put(HashMap.class, MAP);
        map.put(LinkedHashMap.class, MAP);
        map.put(IdentityHashMap.class, MAP);
        map.put(Hashtable.class, MAP);
        map.put(Properties.class, MAP);
        map.put(TreeMap.class, MAP);
        map.put(EnumMap.class, MAP);
        map.put(ConcurrentHashMap.class, MAP);
        map.put(ConcurrentSkipListMap.class, MAP);
        map.put(WeakHashMap.class, MAP);
        map.put(AbstractMap.class, MAP);

        // Collections
        map.put(List.class, COLLECTION);
        map.put(ArrayList.class, COLLECTION);
        map.put(CopyOnWriteArrayList.class, COLLECTION);
        map.put(LinkedList.class, COLLECTION);
        map.put(AbstractList.class, COLLECTION);
        map.put(Set.class, COLLECTION);
        map.put(HashSet.class, COLLECTION);
        map.put(EnumSet.class, COLLECTION);
        map.put(TreeSet.class, COLLECTION);
        map.put(LinkedHashSet.class, COLLECTION);
        map.put(CopyOnWriteArraySet.class, COLLECTION);
        map.put(AbstractSet.class, COLLECTION);
        map.put(ConcurrentSkipListSet.class, COLLECTION);

        // Date
        map.put(Date.class, DATE);
        map.put(Timestamp.class, DATE);
        map.put(java.sql.Date.class, DATE);

        return map;
    }

    public static final class PrimitiveMapper implements MapperTypeAdapter {

        private PrimitiveMapper() {}

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            if(value instanceof String)
                return new AbstractPrimitive((String) value);
            return new AbstractPrimitive((Number) value);
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if(type.equals(String.class)) {
                if(!element.isString())
                    throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
                return element.string();
            }
            if(type.equals(Boolean.class) || type.equals(boolean.class)) {
                if(!element.isBoolean())
                    throw new MapperWrongTypeException(context.getField().getName(), "boolean", Helpers.typeName(element));
                return element.bool();
            }
            if(Number.class.isAssignableFrom(type) || type.isPrimitive()) {
                if(!element.isNumber())
                    throw new MapperWrongTypeException(context.getField().getName(), "number", Helpers.typeName(element));
                if(type.equals(int.class) || type.equals(Integer.class))
                    return element.number().intValue();
                if(type.equals(long.class) || type.equals(Long.class))
                    return element.number().longValue();
                if(type.equals(short.class) || type.equals(Short.class))
                    return element.number().shortValue();
                if(type.equals(double.class) || type.equals(Double.class))
                    return element.number().doubleValue();
                if(type.equals(float.class) || type.equals(Float.class))
                    return element.number().floatValue();
                if(type.equals(byte.class) || type.equals(Byte.class))
                    return element.number().byteValue();
                if(type.equals(Number.class))
                    return element.number();
                return element.number();
            }
            throw new MapperWrongTypeException(context.getField().getName(), "primitive", Helpers.typeName(element));
        }

    }

    public static final class CollectionMapper implements MapperTypeAdapter {

        private CollectionMapper() {}

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            Collection<Object> collection = (Collection<Object>) value;
            AbstractArray array = new AbstractArray();
            collection.forEach(e -> array.add(context.getMapper().map(e)));
            return array;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if(!element.isArray())
                throw new MapperWrongTypeException(context.getField().getName(), "array", Helpers.typeName(element));
            if(type.equals(List.class) || type.equals(AbstractList.class))
                type = ArrayList.class;
            if(type.equals(Set.class))
                type = HashSet.class;
            try {
                Collection<Object> collection = (Collection<Object>) type.newInstance();
                Class<?>[] genericTypes = context.getGenericTypes();
                Class<?> elementType = genericTypes.length > 0 ? genericTypes[0] : null;
                if(elementType == null) {
                    for(AbstractElement e : element.array()) {
                        elementType = Helpers.guessGeneric(e);
                        if(elementType != null)
                            break;
                    }
                }
                for(AbstractElement e : element.array())
                    collection.add(context.getMapper().map(e, elementType));
                return collection;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static final class MapMapper implements MapperTypeAdapter {

        private MapMapper() {}

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            Map<Object, Object> map = (Map<Object, Object>) value;
            AbstractObject object = new AbstractObject();
            map.forEach((k, v) -> object.set(k.toString(), context.getMapper().map(v)));
            return object;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if(!element.isObject())
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            if(type.equals(Map.class) || type.equals(AbstractMap.class))
                type = HashMap.class;
            try {
                Map<Object, Object> map = (Map<Object, Object>) type.newInstance();
                Class<?>[] genericTypes = context.getGenericTypes();
                Class<?> keyType = genericTypes.length > 0 ? genericTypes[0] : String.class;
                Class<?> valueType = genericTypes.length > 1 ? genericTypes[1] : null;
                if(valueType == null) {
                    for(AbstractElement e : element.object().values()) {
                        valueType = Helpers.guessGeneric(e);
                        if(valueType != null)
                            break;
                    }
                }
                for(String k : element.object().keys())
                    map.put(context.getMapper().map(new AbstractPrimitive(k), keyType), context.getMapper().map(element.object().get(k), valueType));
                return map;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static final class DateMapper implements MapperTypeAdapter {

        private DateMapper() {}

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            if(value instanceof Date)
                return new AbstractPrimitive(context.getMapper().getDateFormat().format((Date) value));
            return null;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if(!element.isString())
                throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
            try {
                if(type.equals(Date.class))
                    return context.getMapper().getDateFormat().parse(element.string());
                if(type.equals(java.sql.Date.class))
                    return new java.sql.Date(context.getMapper().getDateFormat().parse(element.string()).getTime());
                if(type.equals(Timestamp.class))
                    return new Timestamp(context.getMapper().getDateFormat().parse(element.string()).getTime());
                throw new MapperException("Unsupported date type '" + type.getName() + "'");
            } catch (ParseException ex) {
                throw new MapperException("Failed to parse date ''" + (context.getField() != null ? (" for field '" + context.getField().getName() + "'") : ""));
            }
        }

    }

    public static final class AbstractMapper implements MapperTypeAdapter {

        private AbstractMapper() {}

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            return (AbstractElement) value;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if(type.equals(AbstractElement.class))
                return element;
            if(type.equals(AbstractNull.class) && !(element instanceof AbstractNull))
                throw new MapperWrongTypeException(context.getField().getName(), "null", Helpers.typeName(element));
            if(type.equals(AbstractPrimitive.class) && !(element instanceof AbstractPrimitive))
                throw new MapperWrongTypeException(context.getField().getName(), "primitive", Helpers.typeName(element));
            if(type.equals(AbstractObject.class) && !(element instanceof AbstractObject))
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            if(type.equals(AbstractArray.class) && !(element instanceof AbstractArray))
                throw new MapperWrongTypeException(context.getField().getName(), "array", Helpers.typeName(element));
            return element;
        }

    }

    public static class FallbackMapper implements MapperTypeAdapter {

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            if(value.getClass().isEnum())
                return new AbstractPrimitive(((Enum<?>) value).name());
            MapperTypeSpec spec = MapperTypeSpec.get(value.getClass());
            if(spec == null)
                throw new MapperException("Unmappable type '" + value.getClass().getName() + "'");
            try {
                AbstractObject object = new AbstractObject();
                for(MapperTypeSpec.FieldSpec fs : spec.getFieldSpecs()) {
                    if(context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                        continue;
                    String k = fs.getName() != null ? fs.getName() : context.getMapper().getNamingPolicy().toAbstract(fs.getField().getName());
                    object.set(k, context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), fs.getField().get(value)));
                }
                if(spec.getAdditionalField() != null) {
                    AbstractObject additional = (AbstractObject) spec.getAdditionalField().get(value);
                    if(additional != null)
                        additional.forEach(object::set);
                }
                return object;
            } catch (IllegalAccessException ex) {
                throw new MapperException(ex.getMessage());
            }
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class type) throws MapperException {
            if(type.isEnum()) {
                if(!element.isString())
                    throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
                try {
                    return Enum.valueOf(type, element.string());
                } catch (IllegalArgumentException ex) {
                    throw new MapperException("There is no enum constant '" + element.string() + "'");
                }
            }
            if(!element.isObject())
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            MapperTypeSpec spec = MapperTypeSpec.get(type);
            if(spec == null)
                throw new MapperException("Unmappable type '" + type.getName() + "'");
            AbstractObject o = element.object();
            try {
                Object obj = type.newInstance();
                AbstractObject additional = spec.getAdditionalField() == null ? null : new AbstractObject();
                List<String> fieldNames = spec.getFieldSpecs().stream().map(s -> s.getField().getName()).collect(Collectors.toList());
                for(String k : o.keys()) {
                    final String fk1 = k;
                    MapperTypeSpec.FieldSpec fs = spec.getFieldSpecs().stream().filter(s -> fk1.equals(s.getName())).findFirst().orElse(null);
                    if(fs != null) {
                        if(context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                            continue;
                        fs.getField().set(obj, context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), o.get(fk1), fs.getField().getType()));
                        continue;
                    }
                    final String fk2 = context.getMapper().getNamingPolicy().fromAbstract(k, fieldNames);
                    fs = spec.getFieldSpecs().stream().filter(s -> s.getName() == null && fk2.equals(s.getField().getName())).findFirst().orElse(null);
                    if(fs != null) {
                        if(context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                            continue;
                        Object val = context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), o.get(fk1),fs.getField().getType());
                        fs.getField().set(obj, val);
                        continue;
                    }
                    if(additional != null)
                        additional.set(fk1, o.get(fk1));
                }
                if(additional != null)
                    spec.getAdditionalField().set(obj, additional);
                return obj;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MapperException(e.getMessage());
            }
        }

    }

}
