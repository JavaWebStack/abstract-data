package org.javawebstack.abstractdata.mapper.naming;

import org.javawebstack.abstractdata.util.Helpers;

import java.util.List;

public class SnakeCaseNamingPolicy implements NamingPolicy {

    public String toAbstract(String source) {
        return String.join("_", Helpers.words(source));
    }

    public String fromAbstract(String source, List<String> fieldNames) {
        return fieldNames.stream().filter(s -> toAbstract(s).equals(source)).findFirst().orElse(source);
    }

}
