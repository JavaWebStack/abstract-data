package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.*;
import org.javawebstack.abstractdata.exception.AbstractCoercingException;
import org.javawebstack.abstractdata.mapper.annotation.DateFormat;
import org.javawebstack.abstractdata.mapper.exception.MapperException;
import org.javawebstack.abstractdata.mapper.exception.MapperWrongTypeException;
import org.javawebstack.abstractdata.util.Helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        for (MapperTypeAdapter adapter : new MapperTypeAdapter[]{
                ABSTRACT,
                PRIMITIVE,
                COLLECTION,
                MAP,
                DATE
        }) {
            for (Class<?> type : adapter.getSupportedTypes())
                map.put(type, adapter);
        }

        return map;
    }

    public static final class PrimitiveMapper implements MapperTypeAdapter {

        private PrimitiveMapper() {
        }

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            if (value instanceof String)
                return new AbstractPrimitive((String) value);
            if (value instanceof Boolean)
                return new AbstractPrimitive((Boolean) value);
            return new AbstractPrimitive((Number) value);
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            try {
                if (type.equals(String.class)) {
                    return element.string(context.getMapper().isStrict());
                }
                if (type.equals(char.class) || type.equals(Character.class)) {
                    if (type.isPrimitive() && element.isNull())
                        throw new MapperWrongTypeException(context.getField().getName(), "number", "null");
                    String s = element.string(context.getMapper().isStrict());
                    if (s.length() != 1)
                        throw new MapperException("Expected string of length 1 for field " + context.getField().getName() + " but received " + s.length());
                    return s.charAt(0);
                }
                if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                    if (type.isPrimitive() && element.isNull())
                        throw new MapperWrongTypeException(context.getField().getName(), "number", "null");
                    return element.bool(context.getMapper().isStrict());
                }
                if (Number.class.isAssignableFrom(type) || type.isPrimitive()) {
                    if (type.isPrimitive() && element.isNull())
                        throw new MapperWrongTypeException(context.getField().getName(), "number", "null");
                    if (type.equals(int.class) || type.equals(Integer.class))
                        return element.number(context.getMapper().isStrict()).intValue();
                    if (type.equals(long.class) || type.equals(Long.class))
                        return element.number(context.getMapper().isStrict()).longValue();
                    if (type.equals(short.class) || type.equals(Short.class))
                        return element.number(context.getMapper().isStrict()).shortValue();
                    if (type.equals(double.class) || type.equals(Double.class))
                        return element.number(context.getMapper().isStrict()).doubleValue();
                    if (type.equals(float.class) || type.equals(Float.class))
                        return element.number(context.getMapper().isStrict()).floatValue();
                    if (type.equals(byte.class) || type.equals(Byte.class))
                        return element.number(context.getMapper().isStrict()).byteValue();
                    if (type.equals(Number.class))
                        return element.number(context.getMapper().isStrict());
                    return element.number(context.getMapper().isStrict());
                }
            } catch (AbstractCoercingException ex) {
                throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
            }
            throw new MapperWrongTypeException(context.getField().getName(), "primitive", Helpers.typeName(element));
        }

        public Class<?>[] getSupportedTypes() {
            return new Class[]{
                    String.class,
                    char.class,
                    Character.class,
                    Integer.class,
                    int.class,
                    Long.class,
                    long.class,
                    Short.class,
                    short.class,
                    Float.class,
                    float.class,
                    Double.class,
                    double.class,
                    Boolean.class,
                    boolean.class,
                    Number.class
            };
        }

    }

    public static final class CollectionMapper implements MapperTypeAdapter {

        private CollectionMapper() {
        }

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            Collection<Object> collection = (Collection<Object>) value;
            AbstractArray array = new AbstractArray();
            collection.forEach(e -> array.add(context.getMapper().map(e)));
            return array;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if (type.equals(List.class) || type.equals(AbstractList.class))
                type = ArrayList.class;
            if (type.equals(Set.class))
                type = HashSet.class;
            try {
                Collection<Object> collection = (Collection<Object>) type.newInstance();
                Class<?>[] genericTypes = context.getGenericTypes();
                Class<?> elementType = genericTypes.length > 0 ? genericTypes[0] : null;
                if (elementType == null) {
                    for (AbstractElement e : element.array(context.getMapper().isStrict())) {
                        elementType = Helpers.guessGeneric(e);
                        if (elementType != null)
                            break;
                    }
                }
                for (AbstractElement e : element.array(context.getMapper().isStrict()))
                    collection.add(context.getMapper().map(e, elementType));
                return collection;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (AbstractCoercingException ex) {
                throw new MapperWrongTypeException(context.getField().getName(), "array", Helpers.typeName(element));
            }
        }

        public Class<?>[] getSupportedTypes() {
            return new Class[]{
                    List.class,
                    ArrayList.class,
                    CopyOnWriteArrayList.class,
                    LinkedList.class,
                    AbstractList.class,
                    Set.class,
                    HashSet.class,
                    EnumSet.class,
                    TreeSet.class,
                    LinkedHashSet.class,
                    CopyOnWriteArraySet.class,
                    AbstractSet.class,
                    ConcurrentSkipListSet.class,
                    Vector.class,
                    Stack.class
            };
        }

    }

    public static final class MapMapper implements MapperTypeAdapter {

        private MapMapper() {
        }

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            Map<Object, Object> map = (Map<Object, Object>) value;
            AbstractObject object = new AbstractObject();
            map.forEach((k, v) -> object.set(k.toString(), context.getMapper().map(v)));
            return object;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if (type.equals(Map.class) || type.equals(AbstractMap.class))
                type = HashMap.class;
            try {
                Map<Object, Object> map = (Map<Object, Object>) type.newInstance();
                Class<?>[] genericTypes = context.getGenericTypes();
                Class<?> keyType = genericTypes.length > 0 ? genericTypes[0] : String.class;
                Class<?> valueType = genericTypes.length > 1 ? genericTypes[1] : null;
                if (valueType == null) {
                    for (AbstractElement e : element.object().values()) {
                        valueType = Helpers.guessGeneric(e);
                        if (valueType != null)
                            break;
                    }
                }
                for (String k : element.object().keys())
                    map.put(context.getMapper().map(new AbstractPrimitive(k), keyType), context.getMapper().map(element.object().get(k), valueType));
                return map;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (AbstractCoercingException ex) {
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            }
        }

        public Class<?>[] getSupportedTypes() {
            return new Class[]{
                    Map.class,
                    HashMap.class,
                    LinkedHashMap.class,
                    IdentityHashMap.class,
                    Hashtable.class,
                    Properties.class,
                    TreeMap.class,
                    EnumMap.class,
                    ConcurrentHashMap.class,
                    ConcurrentSkipListMap.class,
                    WeakHashMap.class,
                    AbstractMap.class
            };
        }

    }

    public static final class DateMapper implements MapperTypeAdapter {

        private DateMapper() {
        }

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            DateFormat df = context.getAnnotation(DateFormat.class);
            if (value instanceof Date) {
                if (df != null && df.epoch()) {
                    long time = ((Date) value).getTime();
                    if (!df.millis())
                        time /= 1000;
                    return new AbstractPrimitive(time);
                }
                java.text.DateFormat dateFormat = (df != null && df.value().length() > 0) ? new SimpleDateFormat(df.value()) : context.getMapper().getDateFormat();
                return new AbstractPrimitive(dateFormat.format((Date) value));
            }
            return null;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            try {
                DateFormat df = context.getAnnotation(DateFormat.class);
                Date date;
                if (df != null && df.epoch()) {
                    long time = element.number(context.getMapper().isStrict()).longValue();
                    if (!df.millis())
                        time *= 1000;
                    date = new Date(time);
                } else {
                    java.text.DateFormat dateFormat = (df != null && df.value().length() > 0) ? new SimpleDateFormat(df.value()) : context.getMapper().getDateFormat();
                    date = dateFormat.parse(element.string(context.getMapper().isStrict()));
                }
                if (type.equals(Date.class))
                    return date;
                if (type.equals(java.sql.Date.class))
                    return new java.sql.Date(date.getTime());
                if (type.equals(Timestamp.class))
                    return new Timestamp(date.getTime());
                throw new MapperException("Unsupported date type '" + type.getName() + "'");
            } catch (ParseException | NumberFormatException | AbstractCoercingException ex) {
                throw new MapperException("Failed to parse date '" + element.string() + "'" + (context.getField() != null ? (" for field '" + context.getField().getName() + "'") : ""));
            }
        }

        public Class<?>[] getSupportedTypes() {
            return new Class[]{
                    Date.class,
                    Timestamp.class,
                    java.sql.Date.class
            };
        }

    }

    public static final class AbstractMapper implements MapperTypeAdapter {

        private AbstractMapper() {
        }

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            return (AbstractElement) value;
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if (type.equals(AbstractElement.class))
                return element;
            if (type.equals(AbstractNull.class) && !(element instanceof AbstractNull))
                throw new MapperWrongTypeException(context.getField().getName(), "null", Helpers.typeName(element));
            if (type.equals(AbstractPrimitive.class) && !(element instanceof AbstractPrimitive))
                throw new MapperWrongTypeException(context.getField().getName(), "primitive", Helpers.typeName(element));
            if (type.equals(AbstractObject.class) && !(element instanceof AbstractObject))
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            if (type.equals(AbstractArray.class) && !(element instanceof AbstractArray))
                throw new MapperWrongTypeException(context.getField().getName(), "array", Helpers.typeName(element));
            return element;
        }

        public Class<?>[] getSupportedTypes() {
            return new Class[]{
                    AbstractElement.class,
                    AbstractNull.class,
                    AbstractPrimitive.class,
                    AbstractArray.class,
                    AbstractObject.class
            };
        }

    }

    public static class FallbackMapper implements MapperTypeAdapter {

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            if (value.getClass().isEnum())
                return new AbstractPrimitive(((Enum<?>) value).name());
            if (value.getClass().equals(UUID.class))
                return new AbstractPrimitive(value.toString());
            MapperTypeSpec spec = MapperTypeSpec.get(value.getClass());
            if (spec == null)
                throw new MapperException("Unmappable type '" + value.getClass().getName() + "'");
            try {
                AbstractObject object = new AbstractObject();
                for (MapperTypeSpec.FieldSpec fs : spec.getFieldSpecs()) {
                    if (context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                        continue;
                    String k = fs.getName() != null ? fs.getName() : context.getMapper().getNamingPolicy().toAbstract(fs.getField().getName());
                    AbstractElement e = context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), fs.getField().get(value));
                    if (e.isNull() && context.getMapper().shouldOmitNull() && fs.shouldOmitNull())
                        continue;
                    object.set(k, e);
                }
                if (spec.getAdditionalField() != null) {
                    AbstractObject additional = (AbstractObject) spec.getAdditionalField().get(value);
                    if (additional != null)
                        additional.forEach(object::set);
                }
                return object;
            } catch (IllegalAccessException ex) {
                throw new MapperException(ex.getMessage());
            }
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class type) throws MapperException {
            if (type.isEnum()) {
                if (!element.isString())
                    throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
                try {
                    return Enum.valueOf(type, element.string());
                } catch (IllegalArgumentException ex) {
                    throw new MapperException("There is no enum constant '" + element.string() + "'");
                }
            }
            if (type.equals(UUID.class))
                return UUID.fromString(element.string());
            if (!element.isObject())
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            MapperTypeSpec spec = MapperTypeSpec.get(type);
            if (spec == null)
                throw new MapperException("Unmappable type '" + type.getName() + "'");
            AbstractObject o = element.object();
            try {
                Constructor constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object obj = constructor.newInstance();
                AbstractObject additional = spec.getAdditionalField() == null ? null : new AbstractObject();
                List<String> fieldNames = spec.getFieldSpecs().stream().map(s -> s.getField().getName()).collect(Collectors.toList());
                for (String k : o.keys()) {
                    final String fk1 = k;
                    MapperTypeSpec.FieldSpec fs = spec.getFieldSpecs().stream().filter(s -> fk1.equals(s.getName())).findFirst().orElse(null);
                    if (fs != null) {
                        if (context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                            continue;
                        fs.getField().set(obj, context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), o.get(fk1), fs.getField().getType()));
                        continue;
                    }
                    final String fk2 = context.getMapper().getNamingPolicy().fromAbstract(k, fieldNames);
                    fs = spec.getFieldSpecs().stream().filter(s -> s.getName() == null && fk2.equals(s.getField().getName())).findFirst().orElse(null);
                    if (fs != null) {
                        if (context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                            continue;
                        Object val = context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), o.get(fk1), fs.getField().getType());
                        fs.getField().set(obj, val);
                        continue;
                    }
                    if (additional != null)
                        additional.set(fk1, o.get(fk1));
                }
                if (additional != null)
                    spec.getAdditionalField().set(obj, additional);
                return obj;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new MapperException(e.getMessage());
            }
        }

    }

}
