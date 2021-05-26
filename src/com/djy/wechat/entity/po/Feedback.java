package com.djy.wechat.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.djy.wechat.dao.annotation.Table;
import com.djy.wechat.entity.po.abs.BaseEntity;

import java.math.BigInteger;

/**
 * @description 反馈信息
 */
@Table(name = "feedback")
public class Feedback extends BaseEntity {
    @JSONField(name = "user_id")
    private BigInteger userId;
    @JSONField(name = "feedback_cont")
    private String feedbackCont;
    /**
     * 页面展示，status的文字
     */
    @JSONField(name = "status_text")
    private String statusText;

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getFeedbackCont() {
        return feedbackCont;
    }

    public void setFeedbackCont(String feedbackCont) {
        this.feedbackCont = feedbackCont;
    }
}
