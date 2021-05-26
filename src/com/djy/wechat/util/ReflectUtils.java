package com.djy.wechat.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @description 用于反射的工具
 */
public class ReflectUtils {

    public static LinkedList<Method> getMethods(Object obj) {
        return getMethods(obj.getClass());
    }

    /**
     * 获取包括private、祖先类在内的所有方法
     * @param clazz
     * @return
     */
    public static LinkedList<Method> getMethods(Class clazz) {
        LinkedList<Method> methods = new LinkedList<>();
        for (Class cla = clazz; cla != Object.class; cla = cla.getSuperclass()) {
            methods.addAll(Arrays.asList(cla.getDeclaredMethods()));
        }
        return methods;
    }

    public static LinkedList<Field> getFields(Object obj) {
        return getFields(obj.getClass());
    }
    /**
     * 获取包括private、祖先类在内的所有属性
     * @param clazz
     * @return
     */
    public static LinkedList<Field> getFields(Class clazz) {
        LinkedList<Field> fields = new LinkedList<>();
        for (Class cla = clazz; cla != Object.class; cla = cla.getSuperclass()) {
            fields.addAll(Arrays.asList(cla.getDeclaredFields()));
        }
        return fields;
    }
}
