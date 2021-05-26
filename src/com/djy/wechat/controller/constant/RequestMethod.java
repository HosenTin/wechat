package com.djy.wechat.controller.constant;

/**
 * request中method的枚举常量
 */
public enum RequestMethod {
    /**
     * 无效请求
     */
    INVALID_REQUEST,
    /**
     * 执行登录方法
     */
    LOGIN_DO,
    /**
     * 执行注册方法
     */
    REGISTER_DO,
    /**
     * 执行退出登录方法
     */
    LOGOUT_DO,
    /**
     * 更新密码
     */
    UPDATEPASSWORD_DO,
    /**
     * 加入聊天
     */
    JOIN_DO,
    /**
     * 退出聊天
     */
    QUIT_DO,
    /**
     * 查找群成员
     *
     */
    MEMBER_DO,
    /**
     * 获取未读消息
     */
    UNREAD_DO,
    /**
     * 将消息设置为已读
     */
    READ_DO,
    /**
     * 删除一个聊天中所有聊天记录
     */
    CLEAR_DO,
    /**
     * 移除群成员
     */
    REMOVE_DO,
    /**
     * 获取用户自己发的朋友圈
     */
    MOMENT_DO,
    /**
     * 获取用户朋友圈中的动态，包括自己的和朋友的
     */
    NEWS_DO,
    /**
     * 点赞和取消点赞
     */
    LOVE_DO,
    /**
     * 朋友圈照片
     */
    PHOTO_DO,
    /**
     * 上传
     */
    UPLOADPHOTO_DO,
    /**
     * 上传文件
     */
    UPLOADFILE_DO,
    /**
     * 更新聊天背景
     */
    BACKGROUND_DO,
    /**
     * 添加
     */
    ADD_DO,
    /**
     * 删除
     */
    DELETE_DO,
    /**
     * 拉黑
     */
    BLACKLIST_DO,
    /**
     * 举报
     */
    REPORT_DO,
    /**
     * 更新
     */
    UPDATE_DO,
    /**
     * 查找一个对象
     */
    GET_DO,
    /**
     * 查找对象集合
     *
     */
    LIST_DO,
    /**
     * 同意举报
     */
    AGREEREPORT_DO,
    /**
     * 同意反馈
     */
    AGREEFEEDBACK_DO,
    /**
     * 发送邮箱验证码
     */
    SENDMAILCODE_DO,
    /**
     * 忘记密码时重置密码
     */
    UPDATEDPASSWORD_DO;
    @Override
    public String toString() {
        return super.toString().toLowerCase().replaceAll("_", ".");
    }
}

