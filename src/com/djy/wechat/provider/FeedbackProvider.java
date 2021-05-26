package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.*;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.service.*;
import com.djy.wechat.service.impl.*;
import com.djy.wechat.util.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.djy.wechat.service.constants.ServiceMessage.PARAMETER_NOT_ENOUGHT;
import static com.djy.wechat.service.constants.Status.ERROR;
import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @description 用于处理反馈相关业务流程
 */
@ActionProvider(path = "/feedback")
public class FeedbackProvider extends Provider {

    private final FeedbackService feedbackService = (FeedbackService) new ServiceProxyFactory().getProxyInstance(new FeedbackServiceImpl());

    /**
     * 提供申诉的业务流程
     */
    @Action(method = RequestMethod.ADD_DO)
    public void addFeedback(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Feedback feedback = (Feedback) BeanUtils.jsonToJavaObject(req.getInputStream(), Feedback.class);
        ServiceResult result;
        //插入申诉内容
        result = feedbackService.addFeedback(feedback);
        returnJsonObject(resp, result);
    }

    /**
     * 提供申诉的业务流程
     */
    @Action(method = RequestMethod.LIST_DO)
    public void listFeedback(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String page = req.getParameter("page");
        ServiceResult result;
        //插入申诉内容
        result = feedbackService.listFeedback(new Integer(page));
        returnJsonObject(resp, result);
    }

    /**
     * 同意举报
     */
    @Action(method = RequestMethod.AGREEFEEDBACK_DO)
    public void agreeFeedback(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String feedbackId = req.getParameter("id");
        ServiceResult result = new ServiceResult();
        // id不能为空
        if (feedbackId == null || "".equals(feedbackId.trim())) {
            result.setMessage(PARAMETER_NOT_ENOUGHT.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        String feedbackUserId = req.getParameter("feedbackUserId");
        //feedbackUserId不能为空
        if (feedbackUserId == null || "".equals(feedbackUserId.trim())) {
            result.setMessage(PARAMETER_NOT_ENOUGHT.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        result = feedbackService.agreeFeedback(feedbackId,feedbackUserId);
        returnJsonObject(resp, result);
    }
}
