package com.djy.wechat.dao.annotation;

import java.lang.annotation.*;

/**
 * @program wechat
 * @description 用于注解查询语句
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {
    String value()default "";
}
