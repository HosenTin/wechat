package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Feedback;

/**
 * 负责申诉相关业务
 */
public interface FeedbackService {
    /**
     * 添加好友关系
     */
    ServiceResult addFeedback(Feedback feedback);

    /**
     * 申诉列表
     * @param page
     * @return
     */
    ServiceResult listFeedback(Integer page);

    /**
     * 通过用户id同意申诉请求
     * @param feedbackId
     * @param feedbackUserId
     * @return
     */
    ServiceResult agreeFeedback(String feedbackId, String feedbackUserId);
}
