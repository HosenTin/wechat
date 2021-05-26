package com.djy.wechat.controller.constant;

/**
 * 界面的地址常量
 */
public enum WebPage {
    /**
     * 注册界面
     */
    REGISTER_JSP,
    /**
     * 网站首页
     */
    INDEX_JSP,
    /**
     * 管理员
     */
    ADMIN_JSP,
    /**
     * 错误页面
     */
    ERROR_JSP,
    /**
     * 登录界面
     */
    LOGIN_JSP,
    /**
     * 忘记密码界面
     */
    FORGET_JSP ;

    @Override
    public String toString() {
        return "/"+super.toString().toLowerCase().replaceAll("_", ".");
    }
}