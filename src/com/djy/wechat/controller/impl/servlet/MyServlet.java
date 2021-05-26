package com.djy.wechat.controller.impl.servlet;

import com.djy.wechat.controller.constant.ControllerMessage;
import com.djy.wechat.provider.Provider;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.djy.wechat.provider.Provider.toErrorPage;

/**
 * @program wechat
 * @description 接收客户端请求，将其转发到controller
 */
@MultipartConfig(location = "D:\\二轮考核\\wechat\\web\\upload")
@WebServlet("/wechat/*")
public class MyServlet extends HttpServlet {
    /**
     * 确保上传目录被创建
     * servlet是单例
     * init初始化方法：获取当前的@MultipartConfig注解—>获取路径配置的值
     * new 文件，父路径是location，子路径就是file/photo
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        MultipartConfig multipartConfig = this.getClass().getAnnotation(MultipartConfig.class);
        String location = multipartConfig.location();
        File fileDir = new File(location, "file");
        File photoDir = new File(location, "photo");
        if (!fileDir.exists()) {
            // 如果不存在，创建
            fileDir.mkdirs();
        }
        if (!photoDir.exists()) {
            // 如果不存在，创建
            photoDir.mkdirs();
        }
        // 必须调用父类的init来赋值config，否则getServletContext()就报npe了
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    /**
     * 负责将请求转发到url对应的Provider
     * (请求的地址)url：http://localhost:8080/wechat/wechat/user?method=register.do
     * 项目名：http://localhost:8080/wechat
     * 请求路径：/wechat/user?method=register.do
     * @param req  请求
     * @param resp 响应
     * @name doPost
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /**
         * 获取到providerMap[先前在ServletContextListener中存入]
         */
        Map<String, Provider> providerMap = (Map<String, Provider>) getServletContext().getAttribute("providerMap");

        String uri = req.getRequestURI();
        /**
         * keySet
         */
        Set<String> keys = providerMap.keySet();
        boolean isMatch=false;
        for (String key : keys) {
            /**
             * providerMap.get(key)获取到xxxProvider对象
             * getPath()父类Provider的方法
             */
            String path =providerMap.get(key).getPath();
            if (uri.substring(14).equalsIgnoreCase(path)) {
                /**
                 * 匹配分发功能
                 */
                providerMap.get(key).doAction(req, resp);
                isMatch=true;
            }
        }
        if(!isMatch){
            toErrorPage(ControllerMessage.REQUEST_INVALID.message,req,resp);
            return;
        }
    }
}


