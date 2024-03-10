package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class YamlParserTest {

    @Test
    public void testParse() {
        AbstractElement e = assertDoesNotThrow(() -> new YamlParser().setSingleRoot(true).parse("test:\n  a: 123\n  b: false\n  c: 'abc'"));
        assertNotNull(e);
        assertTrue(e.isObject());
        assertEquals(1, e.object().size());
        AbstractElement testElement = e.object().get("test");
        assertNotNull(testElement);
        assertTrue(testElement.isObject());
        assertEquals(3, testElement.object().size());
        AbstractElement aElement = testElement.object().get("a");
        assertNotNull(aElement);
        assertTrue(aElement.isNumber());
        assertEquals(123, aElement.number());
        AbstractElement bElement = testElement.object().get("b");
        assertNotNull(bElement);
        assertTrue(bElement.isBoolean());
        assertEquals(false, bElement.bool());
        AbstractElement cElement = testElement.object().get("c");
        assertNotNull(cElement);
        assertTrue(cElement.isString());
        assertEquals("abc", cElement.string());
    }

    @Test
    public void testParseEmptyDocument() {
        assertThrows(ParseException.class, () -> new YamlParser().parse(""));
    }

    @Test
    public void testParseNull() {
        AbstractElement e = assertDoesNotThrow(() -> new YamlParser().parse("null"));
        assertNotNull(e);
        assertTrue(e.isNull());
    }

    @Test
    public void testParseInvalidDocument() {
        assertThrows(ParseException.class, () -> new YamlParser().parse("\"a\"1"));
    }

    @Test
    public void testSetSingleRoot() {
        YamlParser parser = new YamlParser();
        assertFalse(parser.isSingleRoot());
        parser.setSingleRoot(true);
        assertTrue(parser.isSingleRoot());
    }

}
