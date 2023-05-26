package org.javawebstack.abstractdata.mapper.annotation;

public @interface DateFormat {

    String value() default "";
    String timezone() default "";
    boolean epoch() default false;
    boolean millis() default false;

}
