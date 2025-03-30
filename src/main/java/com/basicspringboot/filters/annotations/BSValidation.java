package com.basicspringboot.filters.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BSValidation {
    String label();
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    boolean required() default true;
}
