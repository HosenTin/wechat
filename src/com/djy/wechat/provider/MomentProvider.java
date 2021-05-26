package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Moment;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.service.MomentService;
import com.djy.wechat.service.impl.MomentServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;

import static com.djy.wechat.util.BeanUtils.jsonToJavaObject;
import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @description 负责朋友圈相关流程
 */
@ActionProvider(path = "/moment")
public class MomentProvider extends Provider {
    private final MomentService momentService = (MomentService) new ServiceProxyFactory().getProxyInstance(new MomentServiceImpl());

    /**
     * 提供发布朋友圈的业务流程
     * @name postMoment
     */
    @Action(method = RequestMethod.ADD_DO)
    public void postMoment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ajaxJsonRequest：请求头application/json获取参数 req.getInputStream()
        // 默认请求头为：application/x-www-form-urlencoded接受参数 req.getParameterMap()/req.getParameter()
        Moment moment = (Moment) jsonToJavaObject(req.getInputStream(), Moment.class);
        ServiceResult result;
        result = momentService.insertMoment(moment);
        returnJsonObject(resp, result);
    }

    /**
     * 提供删除朋友圈的业务流程
     * @name deleteMoment
     */
    @Action(method = RequestMethod.DELETE_DO)
    public void deleteMoment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String momentId = req.getParameter("moment_id");
        ServiceResult result;
        result = momentService.removeMoment(new BigInteger(momentId));
        returnJsonObject(resp, result);
    }

    /**
     * 提供获取个人朋友圈的业务流程
     * @name listMoment
     */
    @Action(method = RequestMethod.MOMENT_DO)
    public void listMoment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("user_id");
        String page = req.getParameter("page");
        ServiceResult result;
        result = momentService.listMyMoment(new BigInteger(userId), new Integer(page));
        returnJsonObject(resp, result);
    }

    /**
     * 提供获取朋友圈动态的业务流程
     * @name listNews
     */
    @Action(method = RequestMethod.NEWS_DO)
    public void listNews(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("user_id");
        String page = req.getParameter("page");
        ServiceResult result;
        BigInteger bigInteger = (userId == null || "".equals(userId)) ? null : new BigInteger(userId);
        result = momentService.listNews(bigInteger, new Integer(page));
        returnJsonObject(resp, result);
    }


    /**
     * 提供获取朋友圈照片的业务流程
     * @name loadPhoto
     */
    @Action(method = RequestMethod.PHOTO_DO)
    public void listPhoto(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("user_id");
        String page = req.getParameter("page");
        ServiceResult result;
        result = momentService.listPhoto(new BigInteger(userId), new Integer(page));
        returnJsonObject(resp, result);
    }
    /**
     * 提供朋友圈点赞的服务
     * @name love
     */
    @Action(method = RequestMethod.LOVE_DO)
    public void love(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("user_id");
        String momentId = req.getParameter("moment_id");
        ServiceResult result;
        result = momentService.love(new BigInteger(userId), new BigInteger(momentId));
        returnJsonObject(resp, result);
    }
}
