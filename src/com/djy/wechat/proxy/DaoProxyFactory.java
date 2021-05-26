package com.djy.wechat.proxy;

import com.djy.wechat.dao.SQLRunner;
import com.djy.wechat.dao.annotation.*;
import com.djy.wechat.dao.impl.SQLRunnerImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @description Dao层动态代理
 */
public class DaoProxyFactory implements InvocationHandler {

    private static SQLRunner executor = new SQLRunnerImpl();
    //饿汉式单例
    private static DaoProxyFactory instance= new DaoProxyFactory();

    /**
     * @param interfaces 接口
     * @return 传入接口的代理对象
     */
    public Object getProxyInstance(Class interfaces) {
        //jdk动态代理：newProxyInstance
        //this：InvocationHandler对象
        return Proxy.newProxyInstance(interfaces.getClassLoader(),
                new Class[]{interfaces}, this);
    }
    /**
     * 单例：构造器私有化
     */
    private DaoProxyFactory() {}
    public static DaoProxyFactory getInstance(){
        return instance;
    }

    /**
     * 实现invoke()方法
     * @param proxy  代理人[jdk]
     * @param method 被代理的方法 比如 insert
     * @param args   被代理方法的参数 比如 值/对象
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getAnnotation(Insert.class) != null) {
            /**
             * 获取method上自定义注解
             * value 默认为“”
             */
            if ("".equalsIgnoreCase(method.getAnnotation(Insert.class).value())) {
                Object obj = args[0];
                /**
                 * 对象
                 * Table注解的name值[表名]
                 */
                return executor.insert(obj, obj.getClass().getAnnotation(Table.class).name());
            } else {
                return executor.executeUpdate(method.getDeclaredAnnotation(Insert.class).value(), args);
            }
        }
        if (method.getAnnotation(Update.class) != null) {
            //默认没有sql语句，使用直接更新对象的方法
            if ("".equalsIgnoreCase(method.getAnnotation(Update.class).value())) {
                Object obj = args[0];
                return executor.update(obj, obj.getClass().getAnnotation(Table.class).name());
            } else {
                return executor.executeUpdate(method.getDeclaredAnnotation(Update.class).value(), args);
            }
        }
        if (method.getAnnotation(Delete.class) != null) {
            //默认没有sql语句，使用直接删除对象的方法
            if ("".equalsIgnoreCase(method.getAnnotation(Delete.class).value())) {
                Object obj = args[0];
                return executor.delete(obj, obj.getClass().getAnnotation(Table.class).name());
            } else {
                return executor.executeUpdate(method.getDeclaredAnnotation(Delete.class).value(), args);
            }
        }
        if (method.getAnnotation(Query.class) != null) {
            /**
             * @Result注解
             */
            ResultType type = method.getAnnotation(Result.class).returns();
            switch (type) {
                case OBJECT:
                    /**
                     * sql语句
                     * args：方法本身的参数 参数值
                     * @Result中entity 类
                     */
                    return executor.queryObject(method.getAnnotation(Query.class).value(), args, method.getAnnotation(Result.class).entity());
                case LIST:
                    return executor.queryList(method.getAnnotation(Query.class).value(), args, method.getAnnotation(Result.class).entity());
                case VALUE:
                    return executor.queryValue(method.getAnnotation(Query.class).value(), args);
                default:
            }
        }
        return method.invoke(proxy, args);
    }


}
