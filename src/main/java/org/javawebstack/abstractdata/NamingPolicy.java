package org.javawebstack.abstractdata;

import com.google.gson.FieldNamingPolicy;

public enum NamingPolicy {
    NONE(FieldNamingPolicy.IDENTITY, org.javawebstack.abstractdata.mapper.naming.NamingPolicy.NONE),
    CAMEL_CASE(FieldNamingPolicy.IDENTITY, org.javawebstack.abstractdata.mapper.naming.NamingPolicy.CAMEL_CASE),
    PASCAL_CASE(FieldNamingPolicy.UPPER_CAMEL_CASE, org.javawebstack.abstractdata.mapper.naming.NamingPolicy.PASCAL_CASE),
    SNAKE_CASE(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES, org.javawebstack.abstractdata.mapper.naming.NamingPolicy.SNAKE_CASE),
    KEBAB_CASE(FieldNamingPolicy.LOWER_CASE_WITH_DASHES, org.javawebstack.abstractdata.mapper.naming.NamingPolicy.KEBAB_CASE);

    private final FieldNamingPolicy gsonPolicy;
    private final org.javawebstack.abstractdata.mapper.naming.NamingPolicy mapperPolicy;

    NamingPolicy(FieldNamingPolicy gsonPolicy, org.javawebstack.abstractdata.mapper.naming.NamingPolicy mapperPolicy) {
        this.gsonPolicy = gsonPolicy;
        this.mapperPolicy = mapperPolicy;
    }

    public FieldNamingPolicy getGsonPolicy() {
        return gsonPolicy;
    }

    public org.javawebstack.abstractdata.mapper.naming.NamingPolicy getMapperPolicy() {
        return mapperPolicy;
    }
}
