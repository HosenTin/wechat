package com.djy.wechat.dao.annotation;

import java.lang.annotation.*;

/**
 * @program wechat
 * @description 用于注解删除语句
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {
    String value()default "";
}
