package org.javawebstack.abstractdata;

import java.util.*;
import java.util.stream.Stream;

public class AbstractArray implements AbstractElement, Iterable<AbstractElement> {

    private final List<AbstractElement> elements;

    public boolean isArray() {
        return true;
    }

    public AbstractArray array() {
        return this;
    }

    public AbstractObject object(String key) {
        return query(key).object();
    }

    public AbstractArray array(String key) {
        return query(key).array();
    }

    public AbstractPrimitive primitive(String key) {
        return query(key).primitive();
    }

    public String string(String key) {
        return query(key).string();
    }

    public Boolean bool(String key) {
        return query(key).bool();
    }

    public Number number(String key) {
        return query(key).number();
    }

    public AbstractObject object(String key, AbstractObject orElse) {
        return query(key, orElse).object();
    }

    public AbstractArray array(String key, AbstractArray orElse) {
        return query(key, orElse).array();
    }

    public AbstractPrimitive primitive(String key, AbstractPrimitive orElse) {
        return query(key, orElse).primitive();
    }

    public String string(String key, String orElse) {
        return query(key, new AbstractPrimitive(orElse)).string();
    }

    public Boolean bool(String key, Boolean orElse) {
        return query(key, new AbstractPrimitive(orElse)).bool();
    }

    public Number number(String key, Number orElse) {
        return query(key, new AbstractPrimitive(orElse)).number();
    }

    public AbstractObject object(int index) {
        return get(index).object();
    }

    public AbstractArray array(int index) {
        return get(index).array();
    }

    public AbstractPrimitive primitive(int index) {
        return get(index).primitive();
    }

    public String string(int index) {
        return get(index).string();
    }

    public Boolean bool(int index) {
        return get(index).bool();
    }

    public Number number(int index) {
        return get(index).number();
    }

    public AbstractObject object(int index, AbstractObject orElse) {
        return get(index, orElse).object();
    }

    public AbstractArray array(int index, AbstractArray orElse) {
        return get(index, orElse).array();
    }

    public AbstractPrimitive primitive(int index, AbstractPrimitive orElse) {
        return get(index, orElse).primitive();
    }

    public String string(int index, String orElse) {
        return get(index, new AbstractPrimitive(orElse)).string();
    }

    public Boolean bool(int index, Boolean orElse) {
        return get(index, new AbstractPrimitive(orElse)).bool();
    }

    public Number number(int index, Number orElse) {
        return get(index, new AbstractPrimitive(orElse)).number();
    }

    public AbstractArray add(AbstractElement element) {
        if (element == null)
            element = AbstractNull.INSTANCE;
        elements.add(element);
        return this;
    }

    public AbstractArray() {
        elements = new ArrayList<>();
    }

    public AbstractArray(Object[] objects) {
        this();
        for (Object o : objects) {
            if (o instanceof AbstractElement)
                elements.add((AbstractElement) o);
            else
                elements.add(AbstractElement.fromAbstractObject(o));
        }
    }

    public AbstractArray(Collection<Object> abstractElements) {
        this(abstractElements.toArray());
    }

    public AbstractArray addNull() {
        return add(AbstractNull.INSTANCE);
    }

    public AbstractArray add(Number value) {
        if (value == null)
            return addNull();
        return add(new AbstractPrimitive(value));
    }

    public AbstractArray add(Boolean value) {
        if (value == null)
            return addNull();
        return add(new AbstractPrimitive(value));
    }

    public AbstractArray add(String value) {
        if (value == null)
            return addNull();
        return add(new AbstractPrimitive(value));
    }

    public AbstractArray setNull(int i) {
        return set(i, AbstractNull.INSTANCE);
    }

    public AbstractArray set(int i, AbstractElement element) {
        if (element == null)
            return setNull(i);
        while (elements.size() <= i)
            addNull();
        elements.set(i, element);
        return this;
    }

    public AbstractArray set(int i, Number value) {
        if (value == null)
            return setNull(i);
        return set(i, new AbstractPrimitive(value));
    }

    public AbstractArray set(int i, Boolean value) {
        if (value == null)
            return setNull(i);
        return set(i, new AbstractPrimitive(value));
    }

    public AbstractArray set(int i, String value) {
        if (value == null)
            return setNull(i);
        return set(i, new AbstractPrimitive(value));
    }

    public AbstractArray remove(int i) {
        elements.remove(i);
        return this;
    }

    public AbstractElement[] toArray() {
        return elements.toArray(new AbstractElement[0]);
    }

    public AbstractElement get(int i) {
        return elements.get(i);
    }

    public AbstractElement get(int index, AbstractElement orElse) {
        AbstractElement value = get(index);
        return (index >= 0 && index < size() && !value.isNull()) ? value : orElse;
    }

    public AbstractElement query(String query) {
        String[] q = query.split("\\.", 2);
        try {
            int index = Integer.parseInt(q[0]);
            AbstractElement e = get(index);
            if(e == null || q.length == 1)
                return e;
            if(e.isObject())
                return e.object().query(q[1]);
            if(e.isArray())
                return e.array().query(q[1]);
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public AbstractElement query(String query, AbstractElement orElse) {
        AbstractElement value = query(query);
        return (value != null && !value.isNull()) ? value : orElse;
    }

    public Stream<AbstractElement> stream() {
        return elements.stream();
    }

    public int size() {
        return elements.size();
    }

    public boolean contains(Object o) {
        for (AbstractElement element : elements) {
            if (o instanceof AbstractElement ? ((AbstractElement) o).toObject().equals(element.toObject()) : element.toObject().equals(o))
                return true;
        }
        return false;
    }

    public AbstractArray clear() {
        elements.clear();
        return this;
    }

    public Iterator<AbstractElement> iterator() {
        return elements.iterator();
    }

    public Object toObject() {
        List<Object> list = new ArrayList<>();
        elements.forEach(e -> list.add(e.toObject()));
        return list;
    }

    public static AbstractArray fromArray(Object[] objects) {
        return new AbstractArray(objects);
    }

    public static AbstractArray fromList(Collection collection) {
        return new AbstractArray(collection);
    }

    public Type getType() {
        return Type.ARRAY;
    }

    public AbstractObject object() {
        AbstractObject o = new AbstractObject();
        for (int i = 0; i < size(); i++)
            o.set(String.valueOf(i), get(i));
        return o;
    }

    public Map<String[], Object> toTree() {
        return object().toTree();
    }

    public AbstractElement clone() {
        AbstractArray array = new AbstractArray();
        forEach(e -> array.add(e.clone()));
        return array;
    }

}
