package com.djy.wechat.dao.annotation;

import java.lang.annotation.*;

/**
 * @description 用于注解User的字段名
 * @program wechat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {
    String name ();
}
