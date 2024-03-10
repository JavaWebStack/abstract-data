package org.javawebstack.abstractdata.collector;

import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.AbstractPrimitive;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractObjectCollectorTest {

    @Test
    public void testCollection() {
        AbstractObject object = Stream.of("a", "bc", "def").parallel().collect(new AbstractObjectCollector<>(s -> s, s -> new AbstractPrimitive(s.length())));
        assertEquals(3, object.size());
        assertEquals(1, object.number("a"));
        assertEquals(2, object.number("bc"));
        assertEquals(3, object.number("def"));
    }

}
