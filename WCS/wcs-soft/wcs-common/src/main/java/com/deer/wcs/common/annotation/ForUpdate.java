package com.deer.wcs.common.annotation;

import java.lang.annotation.*;

/**
 * 用来注解哪些字段需要跟新记录
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForUpdate {
    String fieldName() default "";
}

