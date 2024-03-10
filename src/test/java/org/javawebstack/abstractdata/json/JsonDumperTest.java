package org.javawebstack.abstractdata.json;

import org.javawebstack.abstractdata.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class JsonDumperTest {

    @Test
    public void testDumpJavaNull() {
        assertEquals("null", new JsonDumper().dump(null));
    }

    @Test
    public void testDumpNull() {
        assertEquals("null", new JsonDumper().dump(AbstractNull.VALUE));
    }

    @Test
    public void testDumpString() {
        assertEquals("\"abc\"", new JsonDumper().dump(new AbstractPrimitive("abc")));
    }

    @Test
    public void testDumpNumber() {
        assertEquals("123.456", new JsonDumper().dump(new AbstractPrimitive(123.456)));
    }

    @Test
    public void testDumpBoolean() {
        assertEquals("true", new JsonDumper().dump(new AbstractPrimitive(true)));
        assertEquals("false", new JsonDumper().dump(new AbstractPrimitive(false)));
    }

    @Test
    public void testDumpObject() {
        assertEquals("{\"a\":1,\"b\":2}", new JsonDumper().dump(new AbstractObject().set("a", 1).set("b", 2)));
    }

    @Test
    public void testDumpArray() {
        assertEquals("[1,2,3]", new JsonDumper().dump(new AbstractArray().add(1).add(2).add(3)));
    }

    @Test
    public void testEscapeStringEscapeSeq() {
        Map<String, String> escapes = new HashMap<>();
        escapes.put("\"", "\\\"");
        escapes.put("\\", "\\\\");
        escapes.put("\b", "\\b");
        escapes.put("\f", "\\f");
        escapes.put("\n", "\\n");
        escapes.put("\r", "\\r");
        escapes.put("\t", "\\t");
        escapes.put("\0", "\\0");
        escapes.put("/", "\\/");
        for(String c : escapes.keySet()) {
            assertEquals("\"" + escapes.get(c) + "\"", new JsonDumper().dump(new AbstractPrimitive(c)));
        }
    }

    @Test
    public void testEscapeStringUnicode() {
        assertEquals("\"\\u001F\"", new JsonDumper().dump(new AbstractPrimitive("\u001F")));
    }

    @Test
    public void testPrettyFlag() {
        AbstractObject o = new AbstractObject()
                .set("a", 1)
                .set("b", new AbstractArray().add("a").add("b"));
        String expectedNonPretty = "{\"a\":1,\"b\":[\"a\",\"b\"]}";
        String expectedPretty = "{\n    \"a\": 1,\n    \"b\": [\n        \"a\",\n        \"b\"\n    ]\n}";
        assertEquals(expectedNonPretty, new JsonDumper().dump(o));
        assertEquals(expectedPretty, new JsonDumper().setPretty(true).dump(o));
    }

    @Test
    public void testIndent() {
        AbstractObject o = new AbstractObject()
                .set("a", 1)
                .set("b", new AbstractArray().add("a").add("b"));
        String defaultIndent = "    ";
        String shortIndent = "  ";
        Function<String, String> expectedFn = indent -> "{\n" + indent + "\"a\": 1,\n" + indent + "\"b\": [\n" + indent + indent + "\"a\",\n" + indent + indent + "\"b\"\n" + indent + "]\n}";
        assertEquals(expectedFn.apply(defaultIndent), new JsonDumper().setPretty(true).dump(o));
        assertEquals(expectedFn.apply(shortIndent), new JsonDumper().setIndent(shortIndent).setPretty(true).dump(o));
    }

    @Test
    public void testNoNewlineOnEmptyObject() {
        AbstractObject object = new AbstractObject().set("a", new AbstractObject());
        String expected = "{\n    \"a\": {}\n}";
        assertEquals(expected, new JsonDumper().setPretty(true).dump(object));
    }

    @Test
    public void testNoNewlineOnEmptyArray() {
        AbstractObject object = new AbstractObject().set("a", new AbstractArray());
        String expected = "{\n    \"a\": []\n}";
        assertEquals(expected, new JsonDumper().setPretty(true).dump(object));
    }

    @Test
    public void testDumpUnknownType() {
        AbstractElement e = mock(AbstractElement.class);
        assertThrows(IllegalArgumentException.class, () -> new JsonDumper().dump(e));
    }

}
