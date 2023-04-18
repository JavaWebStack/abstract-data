package org.javawebstack.abstractdata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractObjectTest {

    @Test
    void testEqualsNormal() {
        AbstractObject first = new AbstractObject()
                .set("a", "b")
                .set("b", "c");
        AbstractObject second = new AbstractObject()
                .set("a", "b")
                .set("b", "c");

        // normal case
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));

        second.set("a", "d");
        assertFalse(first.equals(second));
        assertFalse(second.equals(first));

        // different sizes
        first.set("d" ,"a");
        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    void testDifferentKeys() {
        AbstractObject first = new AbstractObject()
                .set("a", "b");
        AbstractObject second = new AbstractObject()
                .set("b", "c");

        assertFalse(first.equals(second));
    }

    @Test
    void testEqualsSpecial() {
        AbstractObject first = new AbstractObject()
                .set("a", "b")
                .set("b", "c");
        assertFalse(first.equals(null));
        assertFalse(first.equals(""));
    }
}