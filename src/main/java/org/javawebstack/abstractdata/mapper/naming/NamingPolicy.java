package org.javawebstack.abstractdata.mapper.naming;

import java.util.List;

public interface NamingPolicy {

    NamingPolicy NONE = new NoneNamingPolicy();
    NamingPolicy CAMEL_CASE = new CamelCaseNamingPolicy();
    NamingPolicy SNAKE_CASE = new SnakeCaseNamingPolicy();
    NamingPolicy KEBAB_CASE = new KebabCaseNamingPolicy();
    NamingPolicy PASCAL_CASE = new PascalCaseNamingPolicy();

    String toAbstract(String source);
    String fromAbstract(String source, List<String> fieldNames);

}
