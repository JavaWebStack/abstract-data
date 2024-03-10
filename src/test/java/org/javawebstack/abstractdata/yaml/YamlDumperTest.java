package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YamlDumperTest {

    @Test
    public void testDump() {
        AbstractObject object = new AbstractObject()
                .set("a", 1)
                .set("b", new AbstractArray());
        String expected = "a: 1\nb: []\n"; // Not actually minified but that's what snakeyaml outputs
        String dumped = new YamlDumper().dump(object);
        assertEquals(expected, dumped);
    }

    @Test
    public void testDumpPretty() {
        AbstractObject object = new AbstractObject()
                .set("a", 1)
                .set("b", new AbstractArray());
        String expected = "a: 1\nb: [\n  ]\n"; // Not actually expected, as it's not pretty but that's what snakeyaml calls pretty
        String dumped = new YamlDumper().setPretty(true).dump(object);
        assertEquals(expected, dumped);
    }

    @Test
    public void testSetPretty() {
        YamlDumper dumper = new YamlDumper();
        assertFalse(dumper.isPretty());
        dumper.setPretty(true);
        assertTrue(dumper.isPretty());
    }

}
