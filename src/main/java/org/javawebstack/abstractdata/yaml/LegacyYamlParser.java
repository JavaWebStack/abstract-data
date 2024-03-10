package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegacyYamlParser {

    protected static AbstractElement parse(String source, boolean singleRoot) throws ParseException {
        if(source.trim().equalsIgnoreCase("null"))
            return AbstractNull.VALUE;
        if(source.trim().isEmpty())
            throw new ParseException("Invalid yaml", 0);
        try {
            Yaml yaml = new Yaml();
            List<Object> list = new ArrayList<>();
            yaml.loadAll(source).forEach(list::add);
            Object object = list;
            if (singleRoot && list.size() == 1) {
                object = list.get(0);
            }
            return AbstractElement.fromObject(object);
        } catch (ParserException e) {
            throw new ParseException(e.getMessage(), e.getProblemMark().getIndex());
        }
    }

}
