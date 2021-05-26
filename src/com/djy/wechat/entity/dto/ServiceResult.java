package com.djy.wechat.entity.dto;

import com.djy.wechat.service.constants.Status;

/**
 * @program wechat
 * @description 用于service层返回处理结果
 */
public class ServiceResult {
    /**
     * 状态码
     */
    private Status status;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应数据
     */
    private Object data;

    public ServiceResult() {
    }

    public ServiceResult(Status status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
