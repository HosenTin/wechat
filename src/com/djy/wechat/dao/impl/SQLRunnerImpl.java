package com.djy.wechat.dao.impl;

import com.djy.wechat.dao.ResultMapper;
import com.djy.wechat.dao.SQLMapper;
import com.djy.wechat.dao.SQLRunner;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.util.JdbcUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.LinkedList;

import static com.djy.wechat.util.ReflectUtils.getFields;
import static com.djy.wechat.util.ReflectUtils.getMethods;
import static com.djy.wechat.util.StringUtils.field2SqlField;

/**
 * @program wechat
 * @description 负责执行sql语句
 */
public class SQLRunnerImpl implements SQLRunner {

    /**
     * 执行一条预编译指令，并且填入参数
     * @param sql    要执行的预编译sql语句
     * @param params 参数,如果没有参数需要填，则赋值为null
     * @return java.lang.Object
     * @name executeUpdate
     */
    @Override
    public int executeUpdate(String sql, Object[] params) {
        Connection conn = JdbcUtils.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            JdbcUtils.setParams(ps, params);
            /**
             * 将ps填入参数后的完整语句赋值给sql
             */
            sql = ps.toString();
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("预编译更新语句异常：" + sql, e);
        } finally {
            JdbcUtils.close(conn);
        }
    }

    /**
     * 将一个对象映射成预编译sql语句并执行
     * 需要一个SqlMapper的具体实现
     *
     * @param obj       更新的对象
     * @param sqlMapper 用于映射sql语句的实现类
     * @return int 执行sql语句后影响记录的行数
     * @name executeUpdate
     * @notice 必须传入一个SqlMapper的实现类用于映射预编译语句
     */
    @Override
    public int executeUpdate(Object obj, SQLMapper sqlMapper) {
        if (obj == null) {
            return 0;
        }
        LinkedList fieldNames = new LinkedList<>();
        LinkedList fieldValues = new LinkedList<>();
        /**
         * 使用fieldMapper将对象映射成要更新的字段名集合和字段值集合
         */
        fieldMapper(obj, fieldNames, fieldValues);
        /**
         * 使用sqlMapper将字段名映射成sql预编译语句，使用paramMapper将字段值映射成属性值数组
         * doMap的实现 —— Lamba表达式 —— 传属性名
         * 调用executeUpdate方法时，先执行上面
         * 遇到第二个参数时sqlMapper —— 执行params里的函数[Lamba表达式]
         */
        String sql = sqlMapper.doMap(fieldNames.toArray());
        //将属性的值[集合] 转变成 数组
        Object[] params = fieldValues.toArray();
        Connection conn = JdbcUtils.getConnection();
        int result = 0;
        /**
         * conn.prepareStatement(sql) 预编译
         */
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            /**
             * 给预编译语句填入参数 ？—>值
             * params：属性的值[数组]
             */
            JdbcUtils.setParams(ps, params);
            /**
             * 将ps填入参数后的完整语句赋值给sql
             */
            sql = ps.toString();
            result = ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("预编译更新语句异常：" + sql, e);
        } finally {
            JdbcUtils.close(conn);
        }
        return result;
    }

    /**
     * 把一个对象插入一张表
     * @param obj 要插入的对象
     * @return int 更新的数据库记录数
     * @name insert
     */
    @Override
    public int insert(Object obj, String table) {
        /**
         * Lamba表达式[sqlMapper对象]
         */
        return obj == null ? 0 : executeUpdate(obj, params -> {
            /**
             * params: 属性名组成的数组
             * updateRunner会传入一个属性值集合
             */
            Object[] fieldNames = params;
            /**
             * 根据属性名生成预编译sql插入语句
             * table：表名
             */
            StringBuilder sql = new StringBuilder("insert into " + table + " (");
            for (Object name : fieldNames) {
                sql.append(name.toString() + ",");
            }
            //截取最后的，拼接)
            sql.setCharAt(sql.length() - 1, ')');
            sql.append(" values (");
            for (int i = 0; i < fieldNames.length; i++) {
                sql.append("?,");
            }
            sql.setCharAt(sql.length() - 1, ')');
            return sql.toString();
        });
    }


    /**
     * 根据传入的表名和id，从该表中更新一条记录
     * @param obj 要更新的记录对应的实体类对象
     * @return int 更新的数据库记录数
     * @name update
     */
    @Override
    public int update(Object obj, String table) {
        return (obj == null) ? 0 : executeUpdate(obj, params -> {
            /**
             * updateRunner会传入一个属性值LinkedList
             */
            Object[] fieldNames = params;
            /**
             * 根据属性名生成预编译sql更新语句
             */
            Object id = null;
            StringBuilder sql = new StringBuilder("update " + table + " set ");
            for (Object name : fieldNames) {
                sql.append(name + " = ?,");
            }
            sql.setCharAt(sql.length() - 1, ' ');
            try {
                for (Class clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                    try {
                        id = clazz.getDeclaredMethod("getId").invoke(obj);
                    } catch (NoSuchMethodException e) {
                        /**
                         * 此处不能终止循环
                         */
                    }
                }
                sql.append(" where id = \"" + id + "\"");
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new DaoException("反射执行getId方法异常：无法执行getId方法", e);
            }
            return sql.toString();
        });
    }

    /**
     * 将一个对象从表中删除
     *
     * @param obj 要删除的对象id
     * @return int
     * @name delete
     */
    @Override
    public int delete(Object obj, String table) {
        return obj == null ? 0 : executeUpdate(obj, params -> {
            /**
             * updateRunner会传入一个属性值LinkedList
             */
            Object[] fieldNames = params;
            /**
             * 根据属性名生成预编译sql更新语句
             */
            StringBuilder sql = new StringBuilder("delete from " + table);
            sql.append(whereMapper(params, "and", "="));
            return sql.toString();
        });
    }


    /**
     * 执行一条预编译指令，并且填入参数
     * @param sql    要执行的预编译sql语句
     * @param params 参数,如果没有参数需要填，则赋值为null即可
     * @param mapper 结果集映射器，需要实现ResultMapper接口的doMap方法
     * @return java.lang.Object
     * @name executeQuery
     */
    @Override
    public Object executeQuery(String sql, Object[] params, ResultMapper mapper) {
        Connection conn = JdbcUtils.getConnection();
        //如果没有参数不用预编译：查询整张表
        if (params == null || params.length == 0) {
            try {
                //直接执行查询
                Statement st = conn.createStatement();
                //通过Lamba表达式处理rs
                ResultSet rs = st.executeQuery(sql);
                return mapper.doMap(rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        /**
         * conn.prepareStatement(sql) 预编译
         */
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            JdbcUtils.setParams(ps, params);
            /**
             * 将ps填入参数后的完整语句赋值给sql
             */
            sql = ps.toString();
            ResultSet rs = ps.executeQuery();
            /**
             * 调用接口中的方法映射结果集，使用时该接口必须有实现类
             */
            return mapper.doMap(rs);
        } catch (SQLException e) {
            throw new DaoException("预编译查询语句异常：" + sql, e);
        } finally {
            JdbcUtils.close(conn);
        }
    }

    /**
     * 执行输入的sql语句，并且将结果以LinkedList的形式返回
     * @param sql    查询的预编译sql语句
     * @param params 预编译的参数,如果没有参数需要填，则赋值为null即可
     * @param clazz  用于映射结果集的实体类,如Friend.class
     * @return java.util.LinkedList
     * @name queryList
     */
    @Override
    public LinkedList queryList(String sql, Object[] params, Class clazz) {
        return (LinkedList) executeQuery(sql, params, rs -> {
             /**
             * ResultMapper的一个实现类，提供将结果集映射为一个List的方法
             * 此类的对象在使用前必须设置一个用于映射结果的实体类
             * @notice 如果查询结果为空，则返回一个空集合
             */
            LinkedList list = new LinkedList<>();
            try {
                //得到sql的结果集(rs)的结构信息，比如字段数、字段名等 —— jdk-api
                ResultSetMetaData md = rs.getMetaData();
                /**
                 * 取出包括父类方法在内的所有方法
                 */
                LinkedList<Method> methods = getMethods(clazz.newInstance());
                /**
                 * 字段数
                 */
                int colCount = md.getColumnCount();
                /**
                 * 数据库计数从0开始
                 */
                String[] setters = new String[colCount + 1];
                //字段名数组
                String[] columns = new String[colCount + 1];
                //对应的set方法数组
                for (int i = 0; i < colCount; i++) {
                    /**
                     *  getColumnLabel取得字段名(_),存在columns数组中，并映射成setter方法数组
                     *  jdbc从1开始：预编译sql语句的问号是从1开始的，jdbc查到的自然也是从1开始
                     */
                    columns[i] = md.getColumnLabel(i + 1);
                    /**
                     * 取得字段名并映射为setter方法名，忽略大小写,存在setters数组中
                     * 通过_分割成字符串数组
                     */
                    String[] split = md.getColumnLabel(i + 1).split("_");
                    StringBuilder setter = new StringBuilder("set");
                    for (int j = 0; j < split.length; j++) {
                        //拼接
                        setter.append(split[j]);
                    }
                    setters[i] = setter.toString();
                }
                //遍历查询记录
                while (rs.next()) {
                    //对象
                    Object obj = clazz.newInstance();
                    //对象属性赋值
                    for (int i = 0; i < colCount; i++) {
                        /**
                         * 执行对应的set方法，设置属性值，忽略大小写
                         */
                        //遍历对象的所有方法[get/set]
                        for (Method ms : methods) {
                            if (ms.getName().equalsIgnoreCase(setters[i])) {
                                //TODO 跟踪从数据库到实体类的映射
                                ms.invoke(obj, rs.getObject(columns[i]));
                            }
                        }
                    }
                    // 加入list
                    list.add(obj);
                }
            } catch (SQLException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new DaoException("映射结果集产生异常：" + e.getMessage(), e);
            }
            return list;
        });
    }

    /**
     * 执行输入的sql语句，并且将结果映射为对象，以对象的形式返回
     * 此方法用于查询单行的数据
     * @param sql    查询的预编译sql语句
     * @param params 预编译的参数,如果没有参数需要填，则赋值为null即可
     * @param clazz  用于映射结果集的实体类
     * @return Object
     * @name queryList
     * @notice 如果查询结果为空返回null
     */
    @Override
    public Object queryObject(String sql, Object[] params, Class clazz) {
        LinkedList list = queryList(sql, params, clazz);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 执行一条sql语句，返回一个字段的值
     *
     * @param sql    查询的预编译sql语句
     * @param params 预编译的参数,如果没有参数需要填，则赋值为null即可
     * @return 查询的字段值
     * @name queryValue
     * @notice 如果查询结果为空，则返回null
     */
    @Override
    public Object queryValue(String sql, Object[] params) {
        return executeQuery(sql, params, new ResultMapper() {
            @Override
            public Object doMap(ResultSet rs) {
                try {
                    if (rs.next()) {
                        return rs.getObject(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new DaoException("查询一个字段产生异常：" + sql, e);
                }
                return null;

            }
        });
    }


    /**
     * 把一个对象映射成为属性名集合和属性值集合
     *
     * @param obj         需要被映射的对象
     * @param fieldNames  将映射的属性名返回在这个集合中
     * @param fieldValues 将映射的属性值返回在这个集合中
     * @name fieldMapper
     */
    @Override
    public void fieldMapper(Object obj, LinkedList fieldNames, LinkedList fieldValues) {
        if (obj == null) {
            return;
        }

        /**
         * 取出包括父类在内的所有方法和属性
         */
        LinkedList<Method> methods = getMethods(obj);
        LinkedList<Field> fields = getFields(obj);
        for (Field field : fields) {
            /**
             * 取出每个属性的值
             */
            for (Method method : methods) {
                if (method.getName().startsWith("get") && method.getName().substring(3).equalsIgnoreCase(field.getName())) {
                    Object value = null;
                    try {
                        value = method.invoke(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new DaoException("反射执行get方法异常：" + method.getName(), e);
                    }
                    /**
                     * 只添加不为null值的字段
                     */
                    if (value != null) {
                        fieldValues.add(value);
                        /**
                         * 取出该属性的名称，映射成数据库字段名
                         * fieldNames: eamil,wechatId
                         */
                        fieldNames.add(field2SqlField(field.getName()));
                    }
                }
            }
        }
    }

    /**
     * @param whereFields where条件的字段名数组
     * @param conj        逻辑连接词：and
     * @return java.lang.String 返回映射之后的预编译where语句
     * @name whereMapper
     * "and "长度为4
     */
    @Override
    public String whereMapper(Object[] whereFields, String conj, String condition) {

        StringBuilder sql = new StringBuilder();
        if (whereFields.length == 0) {
            return sql.toString();
        }
        sql.append(" where ");
        for (int i = 0; i < whereFields.length; i++) {
            sql.append(whereFields[i] + " " + condition + "?  " + conj + " ");

        }
        sql.delete(sql.length() - 4, sql.length());
        return sql.toString();
    }
}
