package com.djy.wechat.controller.impl.filter;

import com.djy.wechat.controller.constant.ControllerMessage;
import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.controller.constant.WebPage;
import com.djy.wechat.dao.ReportDao;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Report;
import com.djy.wechat.entity.po.User;
import com.djy.wechat.provider.UserProvider;
import com.djy.wechat.service.constants.Status;
import com.djy.wechat.service.impl.UserServiceImpl;
import com.djy.wechat.util.DateUtil;
import com.djy.wechat.util.ExcludeResourceUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @program wechat
 * @description 负责过滤需要登录的页面的请求
 */
@WebFilter(
        filterName = "LoginFilter",
        urlPatterns = {"/*"}, servletNames = {"/*"},
        initParams = {
                @WebInitParam(name = "ENCODING", value = "UTF-8")
        })
public class LoginFilter implements Filter {

    private final UserProvider userProvider = new UserProvider();
    private final ReportDao reportDao = (ReportDao) DaoProxyFactory.getInstance().getProxyInstance(ReportDao.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //转换成HttpServlet拿到Session(getSession）[Servlet不行]
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        //method = xxx.do
        String method = req.getParameter("method");
        String uri = req.getRequestURI();
        if (!ExcludeResourceUtil.shouldExclude(uri)) {
            String contextPath = req.getContextPath();
            String path = uri.substring(contextPath.length());
            HttpSession sess = req.getSession(false);

            //尝试自动登录
            userProvider.autoLogin(req);
            sess = req.getSession();
            /**
             * 登录/注册/验证码/发送验证码 进行放行
             * 如果是主页，则检查session
             */
            if (sess == null || sess.getAttribute("login") == null) {
                if (WebPage.LOGIN_JSP.toString().equalsIgnoreCase(path) ||
                        (WebPage.REGISTER_JSP.toString()).equalsIgnoreCase(path) ||
                        (WebPage.FORGET_JSP.toString()).equalsIgnoreCase(path) ||
                        (RequestMethod.LOGIN_DO.toString()).equalsIgnoreCase(method) ||
                        (RequestMethod.REGISTER_DO.toString()).equalsIgnoreCase(method) ||
                        (RequestMethod.SENDMAILCODE_DO.toString()).equalsIgnoreCase(method) ||
                        (RequestMethod.UPDATEDPASSWORD_DO.toString()).equalsIgnoreCase(method) ||
                        path.endsWith("logo.png") || path.endsWith(".js")
                        || "/code".equalsIgnoreCase(path)) {
                    //过滤链
                    filterChain.doFilter(req, resp);
                    return;
                } else {
                    //检查session的节点是否含有login，没有则重定向至登录页面
                    if (sess == null || sess.getAttribute("login") == null) {
                        req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req, resp);
                        return;
                    }
                }
            } else {
                User user = (User) sess.getAttribute("login");
                //已登录用户检查登录身份
                if (path.startsWith("/wechat/moment") || path.startsWith("/wechat/friend")
                        || "join.do".equalsIgnoreCase(method)
                        || "add.do".equalsIgnoreCase(method)) {
                    if (user != null && UserServiceImpl.VISITOR_EMAIL.equals(user.getEmail())) {
                        //游客不可使用朋友圈和群聊功能
                        returnJsonObject(resp, new ServiceResult(Status.ERROR, ControllerMessage.YOU_ARE_VISITOR.message, null));
                        return;
                    }
                }
                // 被举报后的权限控制
                if (user != null) {
                    // 查询当前用户是否被人举报过
                    List<Report> list = reportDao.queryReport(user.getId(), DateUtil.getString(new Date()));
                    if (list != null && !list.isEmpty()) {
                        // 可以进行申诉 注销
                        if ((path.startsWith("/wechat/feedback") && "add.do".equals(method))
                            || (path.startsWith("/wechat/user") && "logout.do".equals(method))) {
                            filterChain.doFilter(req, resp);
                            return;
                            // 不进行return的话后面的filterChain.doFilter(req, resp)
                        }else {
                            // 处于封禁状态，没权限
                            returnJsonObject(resp, new ServiceResult(Status.ERROR, ControllerMessage.REPORTED.message, null));
                            return;
                        }
                    }
                }
            }
        }
        filterChain.doFilter(req, resp);
    }
}
