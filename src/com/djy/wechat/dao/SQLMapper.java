package com.djy.wechat.dao;

/**
 * @program XHotel
 * @description 用于将属性映射成sql预编译语句
 */
public interface SQLMapper {


    /**
     * 用于将一个或多个参数映射成预编译sql语句
     *
     * @param params 可变参数，允许传递多个映射sql语句所需的源数据
     * @return 返回映射之后的预编译sql语句
     * @name doMap
     */
    String doMap(Object... params);

}
