package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;

public class YamlParser {

    boolean singleRoot = false;

    public YamlParser setSingleRoot(boolean singleRoot) {
        this.singleRoot = singleRoot;
        return this;
    }

    public boolean isSingleRoot() {
        return singleRoot;
    }

    public AbstractElement parse(String source) {
        return LegacyYamlParser.parse(source, singleRoot);
    }

}
