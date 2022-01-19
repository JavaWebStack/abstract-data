package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.*;
import org.javawebstack.abstractdata.mapper.exception.MapperException;
import org.javawebstack.abstractdata.mapper.exception.MapperWrongTypeException;
import org.javawebstack.abstractdata.util.Helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
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

        for(MapperTypeAdapter adapter : new MapperTypeAdapter[] {
                ABSTRACT,
                PRIMITIVE,
                COLLECTION,
                MAP,
                DATE
        }) {
            for(Class<?> type : adapter.getSupportedTypes())
                map.put(type, adapter);
        }

        return map;
    }

    public static final class PrimitiveMapper implements MapperTypeAdapter {

        private PrimitiveMapper() {}

        public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
            if(value instanceof String)
                return new AbstractPrimitive((String) value);
            if(value instanceof Boolean)
                return new AbstractPrimitive((Boolean) value);
            return new AbstractPrimitive((Number) value);
        }

        public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
            if(type.equals(String.class)) {
                if(!element.isString())
                    throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
                return element.string();
            }
            if(type.equals(char.class) || type.equals(Character.class)) {
                if(!element.isBoolean())
                    throw new MapperWrongTypeException(context.getField().getName(), "string", Helpers.typeName(element));
                String s = element.string();
                if(s.length() != 1)
                    throw new MapperException("Expected string of length 1 for field " + context.getField().getName() + " but received " + s.length());
                return s.charAt(0);
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

        public Class<?>[] getSupportedTypes() {
            return new Class[] {
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

        public Class<?>[] getSupportedTypes() {
            return new Class[] {
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
                    ConcurrentSkipListSet.class
            };
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

        public Class<?>[] getSupportedTypes() {
            return new Class[] {
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

        public Class<?>[] getSupportedTypes() {
            return new Class[] {
                    Date.class,
                    Timestamp.class,
                    java.sql.Date.class
            };
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

        public Class<?>[] getSupportedTypes() {
            return new Class[] {
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
            if(value.getClass().isEnum())
                return new AbstractPrimitive(((Enum<?>) value).name());
            if(value.getClass().equals(UUID.class))
                return new AbstractPrimitive(value.toString());
            MapperTypeSpec spec = MapperTypeSpec.get(value.getClass());
            if(spec == null)
                throw new MapperException("Unmappable type '" + value.getClass().getName() + "'");
            try {
                AbstractObject object = new AbstractObject();
                for(MapperTypeSpec.FieldSpec fs : spec.getFieldSpecs()) {
                    if(context.getMapper().isExposeRequired() ? !fs.isExpose() : fs.isHidden())
                        continue;
                    String k = fs.getName() != null ? fs.getName() : context.getMapper().getNamingPolicy().toAbstract(fs.getField().getName());
                    AbstractElement e = context.getMapper().map(new MapperContext(context.getMapper(), fs.getField(), fs.getAnnotations()).adapter(fs.getAdapter()), fs.getField().get(value));
                    if(e.isNull() && context.getMapper().shouldOmitNull() && fs.shouldOmitNull())
                        continue;
                    object.set(k, e);
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
            if(type.equals(UUID.class))
                return UUID.fromString(element.string());
            if(!element.isObject())
                throw new MapperWrongTypeException(context.getField().getName(), "object", Helpers.typeName(element));
            MapperTypeSpec spec = MapperTypeSpec.get(type);
            if(spec == null)
                throw new MapperException("Unmappable type '" + type.getName() + "'");
            AbstractObject o = element.object();
            try {
                Constructor constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object obj = constructor.newInstance();
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
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new MapperException(e.getMessage());
            }
        }

    }

}
