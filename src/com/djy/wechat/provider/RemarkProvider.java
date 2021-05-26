package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Remark;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.service.RemarkService;
import com.djy.wechat.service.impl.RemarkServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;

import static com.djy.wechat.util.BeanUtils.jsonToJavaObject;
import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @description 负责评论的业务流程
 */
@ActionProvider(path = "/remark")
public class RemarkProvider extends Provider {

    RemarkService remarkService = (RemarkService) new ServiceProxyFactory().getProxyInstance(new RemarkServiceImpl());
    /**
     * 提供发布评论的业务流程
     * @name postRemark
     */
    @Action(method = RequestMethod.ADD_DO)
    public void postRemark(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Remark remark = (Remark) jsonToJavaObject(req.getInputStream(), Remark.class);
        ServiceResult result;
        result = remarkService.addRemark(remark);
        returnJsonObject(resp, result);
    }

    /**
     * 提供获取朋友圈评论的业务流程
     * @name listRemark
     */
    @Action(method = RequestMethod.LIST_DO)
    public void listRemark(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String momentId = req.getParameter("moment_id");
        String page = req.getParameter("page");
        ServiceResult result;
        result = remarkService.listRemark(new BigInteger(momentId), Integer.parseInt(page));
        returnJsonObject(resp, result);
    }

    /**
     * 提供删除朋友圈评论的业务流程
     * @name deleteRemark
     */
    @Action(method = RequestMethod.DELETE_DO)
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String remarkId = req.getParameter("remark_id");
        ServiceResult result;
        result = remarkService.removeRemark(new BigInteger(remarkId));
        returnJsonObject(resp, result);
    }
}
