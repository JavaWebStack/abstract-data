package org.javawebstack.abstractdata.mapper.naming;

import org.javawebstack.abstractdata.util.Helpers;

import java.util.List;

public class CamelCaseNamingPolicy implements NamingPolicy {

    public String toAbstract(String source) {
        List<String> words = Helpers.words(source);
        StringBuilder sb = new StringBuilder(words.get(0));
        for (int i = 1; i < words.size(); i++)
            sb.append(Character.toUpperCase(words.get(i).charAt(0))).append(words.get(i).substring(1));
        return sb.toString();
    }

    public String fromAbstract(String source, List<String> fieldNames) {
        return fieldNames.stream().filter(s -> toAbstract(s).equals(source)).findFirst().orElse(source);
    }

}
