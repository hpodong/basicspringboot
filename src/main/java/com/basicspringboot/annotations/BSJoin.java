package com.basicspringboot.annotations;

import com.basicspringboot.models._BSModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BSJoin {
    Class<? extends _BSModel> table();
}
