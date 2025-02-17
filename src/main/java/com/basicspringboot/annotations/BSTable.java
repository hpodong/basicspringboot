package com.basicspringboot.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BSTable {
    String name();

    String primaryKey() default "";

    String createdAt() default "";

    String updatedAt() default "";

    String deletedAt() default "";

    String status() default "";
}
