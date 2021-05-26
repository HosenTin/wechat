package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.service.UploadService;
import com.djy.wechat.service.impl.UploadServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @description 负责上传文件流程
 */
@ActionProvider(path = "/upload")
public class UploadProvider extends Provider {
    private final UploadService uploadService = (UploadService) new ServiceProxyFactory().getProxyInstance(new UploadServiceImpl());

    /**
     * 上传图片
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    @Action(method = RequestMethod.UPLOADPHOTO_DO)
    public void uploadPhoto(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Part part = req.getPart("photo");
        Object id = req.getParameter("id");
        String tableName = req.getParameter("table");
        ServiceResult result;
        result = uploadService.uploadPhoto(part, id, tableName);
        returnJsonObject(resp, result);
    }

    /**
     * 更改背景图
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    @Action(method = RequestMethod.BACKGROUND_DO)
    public void uploadBackground(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Part part = req.getPart("photo");
        Object id = req.getParameter("id");
        ServiceResult result;
        result = uploadService.uploadBackground(part, id);
        returnJsonObject(resp, result);
    }

    /**
     * 上传文件
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    @Action(method = RequestMethod.UPLOADFILE_DO)
    public void uploadfile(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Part part = req.getPart("file");
        ServiceResult result;
        result = uploadService.uploadFile(part);
        returnJsonObject(resp, result);
    }
}
