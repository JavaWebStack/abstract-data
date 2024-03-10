package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class LegacyYamlDumper {

    protected static String dump(AbstractElement e, boolean pretty) {
        Yaml yaml;
        if (pretty) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            yaml = new Yaml(options);
        } else {
            yaml = new Yaml();
        }
        return yaml.dump(e.toObject());
    }

}
