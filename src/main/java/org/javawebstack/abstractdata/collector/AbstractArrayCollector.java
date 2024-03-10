package org.javawebstack.abstractdata.collector;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AbstractArrayCollector<T> implements Collector<T, List<AbstractElement>, AbstractArray> {

    private final Function<T, AbstractElement> mappingFunction;

    public AbstractArrayCollector(Function<T, AbstractElement> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    public Supplier<List<AbstractElement>> supplier() {
        return ArrayList::new;
    }

    public BiConsumer<List<AbstractElement>, T> accumulator() {
        return (l, e) -> l.add(mappingFunction.apply(e));
    }

    public BinaryOperator<List<AbstractElement>> combiner() {
        return (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
    }

    public Function<List<AbstractElement>, AbstractArray> finisher() {
        return l -> {
            AbstractArray a = new AbstractArray();
            l.forEach(a::add);
            return a;
        };
    }

    public Set<Characteristics> characteristics() {
        Set<Characteristics> characteristics = new HashSet<>();
        return characteristics;
    }

}
