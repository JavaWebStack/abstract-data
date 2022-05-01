package org.javawebstack.abstractdata.mapper.naming;

import org.javawebstack.abstractdata.util.Helpers;

import java.util.List;
import java.util.stream.Collectors;

public class PascalCaseNamingPolicy implements NamingPolicy {

    public String toAbstract(String source) {
        return Helpers.words(source).stream().map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1)).collect(Collectors.joining());
    }

    public String fromAbstract(String source, List<String> fieldNames) {
        return fieldNames.stream().filter(s -> toAbstract(s).equals(source)).findFirst().orElse(source);
    }

}
