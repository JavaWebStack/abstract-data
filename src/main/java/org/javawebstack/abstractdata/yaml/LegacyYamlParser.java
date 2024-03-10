package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;

public class LegacyYamlParser {

    protected static AbstractElement parse(String source, boolean singleRoot) {
        Yaml yaml = new Yaml();
        Object object = yaml.load(source);
        if (singleRoot && object instanceof List) {
            List<Object> list = (List<Object>) object;
            if (list.size() == 0) {
                object = new HashMap<>();
            } else {
                object = list.get(0);
            }
        }
        return AbstractElement.fromObject(object);
    }

}
