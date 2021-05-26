package com.djy.wechat.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;

import static com.djy.wechat.util.ReflectUtils.getFields;
import static com.djy.wechat.util.ReflectUtils.getMethods;
import static com.djy.wechat.util.StringUtils.toLegalText;

/**
 * @description 用于将请求参数映射为Javabean对象
 */
public class BeanUtils {

    /**
     * 负责将request中的parameterMap映射成为一个对象
     * @param map   request中的getParameterMap返回的Map
     * @param clazz 映射的目标类，将返回该类的一个实例
     * @return java.lang.Object
     */
    public static Object toObject(Map<String, String[]> map, Class clazz) {
        //方法
        LinkedList<Method> methods = null;
        //属性
        LinkedList<Field> fields = null;
        Object obj;
        try {
            // User.class user对象：User user = new User();
            obj = clazz.newInstance();
            methods = getMethods(obj);
            fields = getFields(obj);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("无法实例化类：" + clazz.getName());
        }


        for (Field f : fields) {
            /**
             * 获取每个属性的String类型参数的构造器
             * 反射：属性.获取属性类型.获取构造器
             */
            Constructor cons = null;
            try {
                // getType()，获取类型
                cons = f.getType().getConstructor(String.class);
            } catch (Exception e) {
                // 可能存在的问题：只能处理简单类型的属性（并且这些简单类型要有以String为参数的构造器才可以）
                // 自定义的类属性大部分处理不了(比如new User() X)，导致属性值丢失 | 自定义的类不能通过对象利用反射来获取构造器
                // new String(String.class) / new Long(String.class) √
            }
            // 构造属性
            String[] param = map.get(f.getName());
            if (param != null && param[0] != null) {
                Object value = null;
                try {
                    if (cons != null) {
                        // 编码格式转换
                        param[0] = new String(param[0].getBytes(), StandardCharsets.UTF_8);
                        // 过滤非法字符
                        param[0] = toLegalText(param[0]);
                        //赋值
                        value = cons.newInstance(param[0]);
                    }
                    for (Method m : methods) {
                        //匹配:方法名和属性名
                        if (m.getName().equalsIgnoreCase(StringUtils.field2SetMethod(f.getName()))) {
                            //反射调用
                            m.invoke(obj, value);
                        }
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {}
            }
        }
        return obj;
    }


    public static Object jsonToJavaObject(InputStream inputStream,Class targetClass) {
        /**
         * 将输入流中的json数据转化成java对象
         * @name jsonToJavaObject
         * @param inputStream 输入json数据的输入流
         */
        JSONObject jsonObject = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String jsonData;
            while ((jsonData = streamReader.readLine()) != null) {
                builder.append(jsonData);
            }
            jsonObject = JSONObject.parseObject(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toJavaObject(jsonObject, targetClass);
    }


}
