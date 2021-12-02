package org.javawebstack.abstractdata.mapper.naming;

import java.util.List;

public class NoneNamingPolicy implements NamingPolicy {

    public String toAbstract(String source) {
        return source;
    }

    public String fromAbstract(String source, List<String> fieldNames) {
        return source;
    }

}
