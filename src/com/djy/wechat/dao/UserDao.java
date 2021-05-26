package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.entity.po.User;

import java.util.Collection;
import java.util.List;

/**
 * @program wechat
 * @description 负责User类CRUD
 */
public interface UserDao extends BaseDao {
    String TABLE = "user";
    String ALL_FIELD = "email,wechat_id,password,signature,name,photo"
            + ",chat_background,location,role_name," + BASE_FIELD;


    /**
     * 通过id查询一个用户
     * @param id
     * @return 用户对象
     * @name getUserById
     */
    @Result(entity = User.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where id = ? ")
    User getUserById(Object id);


    /**
     * 通过邮箱查询一个用户
     * @param email 账户邮箱
     * @return 用户对象
     * @name getUserByEmail
     */
    @Result(entity = User.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where email = ? ")
    User getUserByEmail(String email);


    /**
     * 通过用户名查询一个用户
     * @param wechatId 微信号
     * @return 用户对象
     * @name getUserByWechatId
     */
    @Result(entity = User.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where wechat_id = ? ")
    User getUserByWechatId(String wechatId);

    /**
     * @param name 用户昵称
     * @name listByName
     */
    @Result(entity = User.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where name = ?")
    List<User> listByName(String name);


    /**
     * 通过关键词模糊查询用户昵称相近的用户
     * @param keyWord 关键词
     * @name listLike
     */
    @Result(entity = User.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where name like ? ")
    List<User> listLikeName(String keyWord);

    /**
     * 检查管理员密码
     * @param email
     * @param roleName
     * @return
     */
    @Result(entity = User.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where email = ? and role_name = ?")
    User checkPassword(String email, String roleName);


}
