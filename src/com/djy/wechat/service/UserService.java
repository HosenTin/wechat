package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.User;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

/**
 * @program wechat
 * @description 负责提供用户服务
 */
public interface UserService {
    /**
     * 检查注册用户的信息是否有效
     * @param user 用户对象
     * @param req 用户输入的邮箱验证码
     * @return 返回传入时的对象
     */
    ServiceResult checkRegister(User user, HttpServletRequest req);

    /**
     * 添加一个用户账号
     * @param user 用户对象
     * @return 返回传入的用户的对象
     */
    ServiceResult insertUser(User user);

    /**
     * 校验用户的密码
     * @param user 用户对象
     * @return 返回传入的用户对象
     */
    ServiceResult checkPassword(User user);

    /**
     * 校验用户名（微信号），是否合法，是否已被占用
     * @param wechatId 微信号
     * @return 返回传入的用户名
     */
    ServiceResult checkWechatId(String wechatId);

    /**
     * 通过用户id获取用户个人信息
     * @param id 用户id
     * @return 返回用户的个人信息
     */
    ServiceResult getUser(Object id);


    /**
     * 更新用户的个人信息,不包括密码，邮箱
     * @param user 用户对象
     * @return 返回传入的用户对象，如果由密码信息/邮箱信息，将被清空
     */
    ServiceResult updateUser(User user);

    /**
     * 更新用户的个人密码
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param userId 用户id
     * @return 返回传入的用户对象，如果由密码信息/邮箱信息，将被清空
     */
    ServiceResult updatePwd(String oldPwd, String newPwd , BigInteger userId);

    /**
     * 返回昵称与传入参数相似的用户列表
     * @param name 用户昵称
     * @return
     * @name listUserLikeName
     */
    ServiceResult listUserLikeName(String name);

    /**
     * 创建一个游客账号，并自动通过登录
     * @name visitorLogin
     */
    ServiceResult visitorLogin();

    /**
     * 忘记密码时重置密码
     * @param userEmail
     * @param newPassword
     * @return
     */
    ServiceResult updatedPwd(String userEmail, String newPassword);
}
