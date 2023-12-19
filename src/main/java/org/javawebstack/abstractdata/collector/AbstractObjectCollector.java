package org.javawebstack.abstractdata.collector;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AbstractObjectCollector<T> implements Collector<T, Map<String, AbstractElement>, AbstractObject> {

    private final Function<T, String> keyFunction;
    private final Function<T, AbstractElement> valueFunction;

    public AbstractObjectCollector(Function<T, String> keyFunction, Function<T, AbstractElement> valueFunction) {
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
    }

    public Supplier<Map<String, AbstractElement>> supplier() {
        return HashMap::new;
    }

    public BiConsumer<Map<String, AbstractElement>, T> accumulator() {
        return (m, e) -> m.put(keyFunction.apply(e), valueFunction.apply(e));
    }

    public BinaryOperator<Map<String, AbstractElement>> combiner() {
        return (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        };
    }

    public Function<Map<String, AbstractElement>, AbstractObject> finisher() {
        return m -> {
            AbstractObject o = new AbstractObject();
            m.forEach(o::set);
            return o;
        };
    }

    public Set<Characteristics> characteristics() {
        Set<Characteristics> characteristics = new HashSet<>();
        characteristics.add(Characteristics.UNORDERED);
        return characteristics;
    }

}
