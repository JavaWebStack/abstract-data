package org.javawebstack.abstractdata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractArrayTest {

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