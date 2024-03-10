package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;

import java.text.ParseException;

public class YamlParser {

    boolean singleRoot = false;

    public YamlParser setSingleRoot(boolean singleRoot) {
        this.singleRoot = singleRoot;
        return this;
    }

    public boolean isSingleRoot() {
        return singleRoot;
    }

    public AbstractElement parse(String source) throws ParseException {
        return LegacyYamlParser.parse(source, singleRoot);
    }

}
