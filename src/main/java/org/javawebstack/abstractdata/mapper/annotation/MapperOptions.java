package org.javawebstack.abstractdata.mapper.annotation;

import org.javawebstack.abstractdata.mapper.MapperTypeAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperOptions {

    String name() default "";

    int order() default 0;

    Class<?>[] generic() default {};

    boolean expose() default false;

    boolean hidden() default false;

    boolean omitNull() default true;

    Class<? extends MapperTypeAdapter> adapter() default MapperTypeAdapter.class;

}
