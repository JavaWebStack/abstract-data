package org.javawebstack.abstractdata;

public enum NamingPolicy {

    NONE(org.javawebstack.abstractdata.mapper.naming.NamingPolicy.NONE),
    CAMEL_CASE(org.javawebstack.abstractdata.mapper.naming.NamingPolicy.CAMEL_CASE),
    PASCAL_CASE(org.javawebstack.abstractdata.mapper.naming.NamingPolicy.PASCAL_CASE),
    SNAKE_CASE(org.javawebstack.abstractdata.mapper.naming.NamingPolicy.SNAKE_CASE),
    KEBAB_CASE(org.javawebstack.abstractdata.mapper.naming.NamingPolicy.KEBAB_CASE);

    private final org.javawebstack.abstractdata.mapper.naming.NamingPolicy mapperPolicy;

    NamingPolicy(org.javawebstack.abstractdata.mapper.naming.NamingPolicy mapperPolicy) {
        this.mapperPolicy = mapperPolicy;
    }

    public org.javawebstack.abstractdata.mapper.naming.NamingPolicy getMapperPolicy() {
        return mapperPolicy;
    }

}
