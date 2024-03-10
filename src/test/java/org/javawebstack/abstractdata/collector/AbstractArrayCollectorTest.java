package org.javawebstack.abstractdata.collector;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractPrimitive;
import org.junit.jupiter.api.Test;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractArrayCollectorTest {

    @Test
    public void testCollection() {
        AbstractArray array = Stream.of("a", "b", "c").parallel().collect(new AbstractArrayCollector<>(AbstractPrimitive::new));
        assertEquals(3, array.size());
        assertEquals("a", array.string(0));
        assertEquals("b", array.string(1));
        assertEquals("c", array.string(2));
    }

}
