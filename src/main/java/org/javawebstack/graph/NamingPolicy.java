package org.javawebstack.graph;

import com.google.gson.FieldNamingPolicy;

public enum NamingPolicy {
    NONE(FieldNamingPolicy.IDENTITY),
    CAMEL_CASE(FieldNamingPolicy.IDENTITY),
    PASCAL_CASE(FieldNamingPolicy.UPPER_CAMEL_CASE),
    SNAKE_CASE(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES),
    KEBAB_CASE(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
    private final FieldNamingPolicy policy;
    NamingPolicy(FieldNamingPolicy policy){
        this.policy = policy;
    }
    public FieldNamingPolicy getGsonPolicy() {
        return policy;
    }
}
