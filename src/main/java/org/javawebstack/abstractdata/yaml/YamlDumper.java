package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;

public class YamlDumper {

    private boolean pretty = false;

    public YamlDumper setPretty(boolean pretty) {
        this.pretty = pretty;
        return this;
    }

    public boolean isPretty() {
        return pretty;
    }

    public String dump(AbstractElement e) {
        return LegacyYamlDumper.dump(e, pretty);
    }

}
