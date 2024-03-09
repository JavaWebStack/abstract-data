package org.javawebstack.abstractdata;

import org.javawebstack.abstractdata.exception.AbstractCoercingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractArrayTest {

    @Test
    void testIsArrayReturnsTrue() {
        assertTrue(new AbstractArray().isArray());
    }

    @Test
    void testObjectThrowsInStrictMode() {
        assertThrows(AbstractCoercingException.class, () -> new AbstractArray().object(true));
    }

    @Test
    void testObjectCoercingWhenNotInStrictMode() {
        AbstractArray a = new AbstractArray();
        a.add(0);
        a.add("1");
        AbstractObject o = assertDoesNotThrow(() -> a.object(false));
        assertTrue(o.hasNumber("0"));
        assertTrue(o.hasString("1"));
    }

    @Test
    void testObjectQueryReturnsNullWhenKeyIsNotFound() {
        assertNull(new AbstractArray().add(new AbstractObject()).object("1"));
    }

    @Test
    void testObjectQueryReturnsValueWhenKeyIsFound() {
        assertNotNull(new AbstractArray().add(new AbstractObject()).object("0"));
    }

    @Test
    void testArrayQueryReturnsNullWhenKeyIsNotFound() {
        assertNull(new AbstractArray().add(new AbstractArray()).array("1"));
    }

    @Test
    void testArrayQueryReturnsValueWhenKeyIsFound() {
        assertNotNull(new AbstractArray().add(new AbstractArray()).array("0"));
    }

    @Test
    void testPrimitiveQueryReturnsNullWhenKeyIsNotFound() {
        assertNull(new AbstractArray().add(0).primitive("1"));
    }

    @Test
    void testPrimitiveQueryReturnsValueWhenKeyIsFound() {
        assertNotNull(new AbstractArray().add(0).primitive("0"));
    }

    @Test
    void testStringQueryReturnsNullWhenKeyIsNotFound() {
        assertNull(new AbstractArray().add("abc").string("1"));
    }

    @Test
    void testStringQueryReturnsValueWhenKeyIsFound() {
        assertEquals("abc", new AbstractArray().add("abc").string("0"));
    }

    @Test
    void testNumberQueryReturnsNullWhenKeyIsNotFound() {
        assertNull(new AbstractArray().add(123).number("1"));
    }

    @Test
    void testNumberQueryReturnsValueWhenKeyIsFound() {
        assertEquals(123, new AbstractArray().add(123).number("0"));
    }

    @Test
    void testBoolQueryReturnsNullWhenKeyIsNotFound() {
        assertNull(new AbstractArray().add(true).bool("1"));
    }

    @Test
    void testBoolQueryReturnsValueWhenKeyIsFound() {
        assertEquals(true, new AbstractArray().add(true).bool("0"));
    }

    @Test
    void testQueryOrElseReturnsValueIfFoundAndNonNull() {
        AbstractElement expected = new AbstractPrimitive(123);
        AbstractElement orElse = new AbstractPrimitive("456");
        AbstractArray array = new AbstractArray().add(expected);
        assertEquals(expected, array.query("0", orElse));
    }

    @Test
    void testQueryOrElseReturnsOrElseIfFoundAndNull() {
        AbstractElement orElse = new AbstractPrimitive("456");
        AbstractArray array = new AbstractArray().addNull();
        assertEquals(orElse, array.query("0", orElse));
    }

    @Test
    void testQueryOrElseReturnsOrElseIfNotFound() {
        AbstractElement unexpected = new AbstractPrimitive(123);
        AbstractElement orElse = new AbstractPrimitive("456");
        AbstractArray array = new AbstractArray().add(unexpected);
        assertEquals(orElse, array.query("1", orElse));
    }

    @Test
    void testObjectQueryOrElseReturnsOrElseIfNotFound() {
        AbstractObject orElse = new AbstractObject().set("a", 1);
        assertSame(orElse, new AbstractArray().object("0", orElse));
    }

    @Test
    void testArrayQueryOrElseReturnsOrElseIfNotFound() {
        AbstractArray orElse = new AbstractArray().add(123);
        assertSame(orElse, new AbstractArray().array("0", orElse));
    }

    @Test
    void testPrimitiveQueryOrElseReturnsOrElseIfNotFound() {
        AbstractPrimitive orElse = new AbstractPrimitive(123);
        assertSame(orElse, new AbstractArray().primitive("0", orElse));
    }

    @Test
    void testStringQueryOrElseReturnsOrElseIfNotFound() {
        String orElse = "abc";
        assertEquals(orElse, new AbstractArray().string("0", orElse));
    }

    @Test
    void testNumberQueryOrElseReturnsOrElseIfNotFound() {
        Number orElse = 123;
        assertEquals(orElse, new AbstractArray().number("0", orElse));
    }

    @Test
    void testBoolQueryOrElseReturnsOrElseIfNotFound() {
        Boolean orElse = true;
        assertEquals(orElse, new AbstractArray().bool("0", orElse));
    }

    @Test
    void testQueryThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> new AbstractArray().query(null));
    }

    @Test
    void testQueryWithNonIntPathReturnsNull() {
        assertNull(new AbstractArray().add(1).query("abc"));
    }

    @Test
    void testQueryWithNonExistingIndexReturnsNull() {
        assertNull(new AbstractArray().add(0).query("1"));
    }

    @Test
    void testQueryWithOnlyOneSegmentReturnsValue() {
        assertNotNull(new AbstractArray().add(1).query("0"));
    }

    @Test
    void testClearClearsTheArrayAndReturnsThis() {
        AbstractArray array = new AbstractArray().add(1).add(2).add(3);
        assertEquals(3, array.size());
        assertSame(array, array.clear());
        assertEquals(0, array.size());
    }

    @Test
    void testQueryWithObjectValueQueriesObject() {
        AbstractArray array = new AbstractArray();
        array.add(new AbstractObject().set("a", 1));
        assertEquals(1, array.query("0.a").number().intValue());
    }

    @Test
    void testQueryWithArrayValueQueriesArray() {
        AbstractArray array = new AbstractArray();
        array.add(new AbstractArray().add(1));
        assertEquals(1, array.query("0.0").number().intValue());
    }

    @Test
    void testQueryWithPrimitiveValueReturnsNull() {
        AbstractArray array = new AbstractArray();
        array.add("abc");
        assertNull(array.query("0.abc"));
    }

    @Test
    void testEqualsNormal() {
        AbstractArray first = new AbstractArray();
        first.add("Hello");
        AbstractArray second = new AbstractArray();
        second.add("World");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));

        second.set(0, "Hello");

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    @Test
    void testEqualsDifferentSizes() {
        AbstractArray first = new AbstractArray();
        first.add("Hello");
        AbstractArray second = new AbstractArray();
        second.add("Hello");
        second.add("World");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    void testEqualsSpecial() {
        AbstractArray first = new AbstractArray();
        first.add("Hello");
        assertFalse(first.equals(""));
        assertFalse(first.equals(null));
    }
}