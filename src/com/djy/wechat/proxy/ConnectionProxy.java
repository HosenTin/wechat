package com.djy.wechat.proxy;

import com.djy.wechat.dao.DataSource;
import com.djy.wechat.exception.DaoException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @description 代理数据库连接对象
 */
public class ConnectionProxy implements InvocationHandler {

    private Connection target;
    private DataSource dataSource;

    public Connection getProxyInstance(Connection target){
        this.target= target;
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class[]{Connection.class},this);
    }

    public ConnectionProxy(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ("close".equals(method.getName())) {
            //调用代理对象的close方法时，将目标对象放回数据库连接池
            dataSource.freeConnection(target);
            return null;
        }
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DaoException("无法调用目标对象的方法", e);
        }
    }

    public Connection getTarget() {
        return target;
    }

    public void setTarget(Connection target) {
        this.target = target;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
