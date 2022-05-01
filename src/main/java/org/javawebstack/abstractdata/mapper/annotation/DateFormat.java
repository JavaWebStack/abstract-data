package org.javawebstack.abstractdata.mapper.annotation;

public @interface DateFormat {

    String value() default "";
    boolean epoch() default false;
    boolean millis() default false;

}
