package org.javawebstack.abstractdata.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {

    String value() default "";

    String timezone() default "";

    boolean epoch() default false;

    boolean millis() default false;

}
