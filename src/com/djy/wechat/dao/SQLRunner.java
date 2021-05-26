package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Insert;

import java.util.LinkedList;

/**
 * @program wechat
 * @description 负责执行SQL语句
 */
public interface SQLRunner {

    /**
     * 执行一条预编译指令，并且填入参数
     *
     * @param sql    要执行的预编译sql语句
     * @param params 参数,如果没有参数需要填，则赋值为null即可
     * @return java.lang.Object
     * @name executeUpdate
     */
    int executeUpdate(String sql, Object[] params);


    /**
     * 将一个对象映射成预编译sql语句并执行<br>
     * 需要一个SqlMapper的具体实现
     *
     * @param obj       更新的对象
     * @param sqlMapper 用于映射sql语句的实现类
     * @return int 执行sql语句后影响记录的行数
     * @name executeUpdate
     * @notice 必须传入一个SqlMapper的实现类用于映射预编译语句
     * @see SQLMapper
     */
    int executeUpdate(Object obj, SQLMapper sqlMapper);

    /**
     * 把一个对象插入一张表
     *
     * @param obj 要插入的对象
     * @return int 更新的数据库记录数
     * @name insert
     */
    @Insert()
    int insert(Object obj, String table);

    /**
     * 根据传入的表名和id，从该表中更新一条记录
     *
     * @param obj 要更新的记录对应的实体类对象
     * @return int 更新的数据库记录数
     * @name update
     */
    int update(Object obj, String table);

    /**
     * 将一个对象从表中删除
     *
     * @param id 要删除的对象id
     * @return int
     * @name delete
     */
    int delete(Object id, String table);


    /**
     * 执行一条预编译指令，并且填入参数
     *
     * @param sql    要执行的预编译sql语句
     * @param params 参数,如果没有参数需要填，则赋值为null即可
     * @param mapper 结果集映射器，需要实现ResultMapper接口的doMap方法
     * @return java.lang.Object
     * @name executeQuery
     */
    Object executeQuery(String sql, Object[] params, ResultMapper mapper);

    /**
     * 执行输入的sql语句，并且将结果以LinkedList的形式返回
     *
     * @param sql    查询的预编译sql语句
     * @param params 预编译的参数,如果没有参数需要填，则赋值为null即可
     * @param clazz  用于映射结果集的实体类
     * @return java.util.LinkedList
     * @name queryList
     */
    LinkedList queryList(String sql, Object[] params, Class clazz);

    /**
     * 执行输入的sql语句，并且将结果映射为对象，以对象的形式返回
     * 此方法用于查询单行的数据
     *
     * @param sql    查询的预编译sql语句
     * @param params 预编译的参数,如果没有参数需要填，则赋值为null即可
     * @param clazz  用于映射结果集的实体类
     * @return Object
     * @name queryList
     * @notice 如果查询结果为空，则返回null
     */
    Object queryObject(String sql, Object[] params, Class clazz);

    /**
     * 执行一条sql语句，返回一个字段的值
     *
     * @param sql    查询的预编译sql语句
     * @param params 预编译的参数,如果没有参数需要填，则赋值为null即可
     * @return 查询的字段值
     * @name queryValue
     * @notice 如果查询结果为空，则返回null
     */
    Object queryValue(String sql, Object[] params);

    /**
     * 把一个对象映射成为属性名集合和属性值集合
     *
     * @param obj         需要被映射的对象
     * @param fieldNames  将映射的属性名返回在这个集合中
     * @param fieldValues 将映射的属性值返回在这个集合中
     * @name fieldMapper
     */
    void fieldMapper(Object obj, LinkedList fieldNames, LinkedList fieldValues);

    /**
     * @param whereFields where条件的字段名数组
     * @param conj        逻辑连接词：and
     * @return java.lang.String 返回映射之后的预编译where语句
     * @name whereMapper
     */
    String whereMapper(Object[] whereFields, String conj, String condition);

}
