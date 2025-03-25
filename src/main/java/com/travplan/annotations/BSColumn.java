package com.travplan.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BSColumn {

    /**
    * 데이터베이스 컬럼명
    */
    String name();

    /**
     * Request 요청 키값
     */
    String reqName() default "";

    /**
     * Response 키값
     */
    String resName() default "";

    /**
     * Response 포함 여부
     * @default false
     */
    boolean isShow() default true;
    /**
     * LIST Response 포함 여부
     * @default false
     */
    boolean isShowList() default true;
    boolean isInput() default true;
    boolean nullable() default false;

    boolean isQuerySelect() default false;
}
