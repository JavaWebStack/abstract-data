package org.javawebstack.abstractdata.json;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractPrimitive;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    @Test
    public void testParseEmptyString() {
        ParseException e = assertThrows(ParseException.class, () -> new JsonParser().parse(""));
        assertEquals("Unexpected character <EOF>", e.getMessage());
    }

    @Test
    public void testParseUnexpectedEOF() {
        ParseException e = assertThrows(ParseException.class, () -> new JsonParser().parse("{"));
        assertEquals("Unexpected character <EOF>", e.getMessage());
        e = assertThrows(ParseException.class, () -> new JsonParser().parse("["));
        assertEquals("Unexpected character <EOF>", e.getMessage());
        e = assertThrows(ParseException.class, () -> new JsonParser().parse("\""));
        assertEquals("Unexpected character <EOF>", e.getMessage());
        e = assertThrows(ParseException.class, () -> new JsonParser().parse("t"));
        assertEquals("Unexpected character <EOF>", e.getMessage());
    }

    @Test
    public void testParseUnexpectedCharacter() {
        ParseException e = assertThrows(ParseException.class, () -> new JsonParser().parse("{\n    \"abc\":x\n}"));
        assertEquals("Unexpected character 'x' at line 2 pos 11", e.getMessage());
        assertEquals(12, e.getErrorOffset());
    }

    @Test
    public void testParseBooleanTrue() {
        AbstractElement trueElement = assertDoesNotThrow(() -> new JsonParser().parse("true"));
        assertTrue(trueElement.isBoolean());
        assertTrue(trueElement.bool());
        assertThrows(ParseException.class, () -> new JsonParser().parse("trux"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("trxx"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("txxx"));
    }

    @Test
    public void testParseBooleanFalse() {
        AbstractElement falseElement = assertDoesNotThrow(() -> new JsonParser().parse("false"));
        assertTrue(falseElement.isBoolean());
        assertFalse(falseElement.bool());
        assertThrows(ParseException.class, () -> new JsonParser().parse("falsx"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("falxx"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("faxxx"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("fxxxx"));
    }

    @Test
    public void testParseBooleanNull() {
        AbstractElement nullElement = assertDoesNotThrow(() -> new JsonParser().parse("null"));
        assertTrue(nullElement.isNull());
        assertThrows(ParseException.class, () -> new JsonParser().parse("nulx"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("nuxx"));
        assertThrows(ParseException.class, () -> new JsonParser().parse("nxxx"));
    }

    @Test
    public void testParseInteger() {
        AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse("123"));
        assertTrue(e.isNumber());
        assertEquals(123, e.number());
    }

    @Test
    public void testParseLong() {
        long expected = 1L + Integer.MAX_VALUE;
        AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse(String.valueOf(expected)));
        assertTrue(e.isNumber());
        assertEquals(expected, e.number());
    }

    @Test
    public void testParseDouble() {
        AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse("123.456"));
        assertTrue(e.isNumber());
        assertEquals(123.456, e.number());
    }

    @Test
    public void testParseStringEscapeSeq() {
        Map<String, String> escapes = new HashMap<>();
        escapes.put("\\\"", "\"");
        escapes.put("\\\\", "\\");
        escapes.put("\\b", "\b");
        escapes.put("\\f", "\f");
        escapes.put("\\n", "\n");
        escapes.put("\\r", "\r");
        escapes.put("\\t", "\t");
        escapes.put("\\0", "\0");
        escapes.put("\\/", "/");
        for(String c : escapes.keySet()) {
            AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse("\"" + c + "\""));
            assertTrue(e.isString());
            assertEquals(escapes.get(c), e.string());
        }
    }

    @Test
    public void testParseStringUnicode() {
        AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse("\"\\u001F\""));
        assertTrue(e.isString());
        assertEquals("\u001F", e.string());
    }

    @Test
    public void testParseValidObject() {
        AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse("{\"a\":1,\"b\":2}"));
        assertTrue(e.isObject());
        assertEquals(2, e.object().size());
        List<String> keys = new ArrayList<>(e.object().keys());
        assertEquals("a", keys.get(0));
        assertEquals("b", keys.get(1));
        AbstractElement aElement = e.object().get("a");
        assertTrue(aElement.isNumber());
        assertEquals(1, aElement.number());
        AbstractElement bElement = e.object().get("b");
        assertTrue(bElement.isNumber());
        assertEquals(2, bElement.number());
    }

    @Test
    public void testParseInvalidObjectWithMissingColon() {
        assertThrows(ParseException.class, () -> new JsonParser().parse("{\"a\"1}"));
    }

    @Test
    public void testParseInvalidObjectWithMissingComma() {
        assertThrows(ParseException.class, () -> new JsonParser().parse("{\"a\":1\"b\":2}"));
    }

    @Test
    public void testParseValidArray() {
        AbstractElement e = assertDoesNotThrow(() -> new JsonParser().parse("[1,2]"));
        assertTrue(e.isArray());
        assertEquals(2, e.array().size());
        AbstractElement firstElement = e.array().get(0);
        assertTrue(firstElement.isNumber());
        assertEquals(1, firstElement.number());
        AbstractElement secondElement = e.array().get(1);
        assertTrue(secondElement.isNumber());
        assertEquals(2, secondElement.number());
    }

    @Test
    public void testParseInvalidArrayWithInvalidValue() {
        assertThrows(ParseException.class, () -> new JsonParser().parse("[talse]"));
    }

    @Test
    public void testParseInvalidArrayWithMissingComma() {
        assertThrows(ParseException.class, () -> new JsonParser().parse("[\"a\"\"b\"]"));
    }

}
