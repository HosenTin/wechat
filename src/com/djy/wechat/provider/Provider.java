package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.controller.constant.WebPage;
import com.djy.wechat.exception.ServiceException;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.util.ControllerUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.djy.wechat.controller.constant.ControllerMessage.REQUEST_INVALID;
import static com.djy.wechat.controller.constant.ControllerMessage.SYSTEM_EXECEPTION;

/**
 * @description 所有ActionProvider的父类，提供共有的doAction方法
 */
public class Provider {
    /**
     * 负责将请求分发到对应的Action方法
     * 比如：注册时
     * (请求的地址)url：http://localhost:8080/wechat/wechat/user?method=register.do
     * 请求地址中的method参数：method=register.do
     * doAction ： 父类方法
     * @param req  请求
     * @param resp 响应
     * @name doAction
     */
    public void doAction(HttpServletRequest req, HttpServletResponse resp) {
        //获取请求地址中的method参数，转换成对应的枚举对象
        try {
            RequestMethod requestMethod = ControllerUtils.valueOf(req.getParameter("method"));
            if (RequestMethod.INVALID_REQUEST.equals(requestMethod)) {
                toErrorPage("无效的访问链接，系统无法识别您的请求指向的服务内容：" + req.getRequestURI(), req, resp);
                return;
            } else {
                boolean isMacth =false;
                //反射获取所有方法[根据方法上的注解找到对应的Action方法并执行]
                Method[] methods = this.getClass().getMethods();
                for (Method m : methods) {
                    Action action = m.getAnnotation(Action.class);
                    if (action != null && action.method().equals(requestMethod)) {
                        try {
                            //反射执行方法
                            m.invoke(this, req, resp);
                            isMacth=true;
                        } catch (ServiceException e) {
                            e.printStackTrace();
                            toErrorPage(e.getMessage(), req, resp);
                            return;
                        }
                    }
                }
                if(!isMacth){
                    toErrorPage("无效的访问链接，系统无法识别您的请求指向的服务内容"+ req.getRequestURI(), req, resp);
                }
            }
        } catch (ExceptionInInitializerError e) {
            e.printStackTrace();
            toErrorPage(SYSTEM_EXECEPTION.message, req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            toErrorPage(REQUEST_INVALID.message, req, resp);
        }
    }

    /**
     * 转发到错误界面，向客户端输出错误信息
     * @param message 异常描述信息
     * @name toErrorPage
     */
    public static void toErrorPage(String message, HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("message", message);
        try {
            req.getRequestDispatcher(WebPage.ERROR_JSP.toString()).forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            try {
                resp.getWriter().write("服务器异常");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 返回XxxProvider的url映射路径
     * 获取当前类上ActionProvider注解—>获取这个注解的path()属性值
     * @name getPath
     */
    public String getPath() {
        return this.getClass().getAnnotation(ActionProvider.class).path();
    }
}
