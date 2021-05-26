package com.djy.wechat.controller.impl.filter;

import com.djy.wechat.controller.constant.ControllerMessage;
import com.djy.wechat.controller.constant.WebPage;
import com.djy.wechat.util.ExcludeResourceUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program wechat
 * @description 负责设置编码格式
 */
@WebFilter(
        filterName = "EncodingFilter",
        urlPatterns = {"/*"}, servletNames = {"/*"},
        initParams = {
                @WebInitParam(name = "ENCODING", value = "UTF-8")
        }
)
public class EncodingFilter implements Filter {

    private String ENCODING = null;

    @Override
    public void init(FilterConfig config) {
        this.ENCODING = config.getInitParameter("ENCODING");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        if (!ExcludeResourceUtil.shouldExclude(req.getRequestURI())) {
            try {
                req.setCharacterEncoding(ENCODING);
                resp.setContentType("text/html;charset=utf-8");
                resp.setCharacterEncoding(ENCODING);
                resp.addHeader("Access-Control-Allow-Origin", "*");
                resp.addHeader("Access-Control-Allow-Headers",
                        "origin, content-type, accept, x-requested-with, sid, mycustom, smuser");
                resp.addHeader("Access-Control-Allow-Methods", "*");
                resp.addHeader("Access-Control-Max-Age", "100");
                resp.addHeader("Access-Control-Allow-Credentials", "false");

                filterChain.doFilter(servletRequest, servletResponse);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
                try {
                    req.setAttribute("message", ControllerMessage.SYSTEM_EXECEPTION.message);
                    req.getRequestDispatcher(WebPage.ERROR_JSP.toString()).forward(req, resp);
                } catch (ServletException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}

