package com.djy.wechat.controller.constant;

/**
 * @description 用于枚举控制层的系统消息
 */
public enum ControllerMessage {
    /**
     * 游客身份
     */
    YOU_ARE_VISITOR("您现在处于游客身份，该服务并未对游客开放，如需使用请先注册一个账号"),
    /**
     * 被举报过
     */
    REPORTED("您已被别人举报,不能使用相关功能，请先申诉"),
    /**
     * 系统故障
     */
    SYSTEM_EXECEPTION("服务器异常"),
    /**
     * 请求参数错误
     */
    REQUEST_INVALID("您的请求参数不足或错误，系统无法处理您的请求");

    public String message;

    ControllerMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }
}
