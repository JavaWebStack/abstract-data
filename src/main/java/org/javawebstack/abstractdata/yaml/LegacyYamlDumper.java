package org.javawebstack.abstractdata.yaml;

import org.javawebstack.abstractdata.AbstractElement;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class LegacyYamlDumper {

    protected static String dump(AbstractElement e, boolean pretty) {
        Yaml yaml;
        DumperOptions options = new DumperOptions();
        if (pretty) {
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
        } else {
            options.setPrettyFlow(false);
        }
        yaml = new Yaml(options);
        return yaml.dump(e.toObject());
    }

}
