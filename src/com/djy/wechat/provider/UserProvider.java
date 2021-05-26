package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.controller.constant.WebPage;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Friend;
import com.djy.wechat.entity.po.Message;
import com.djy.wechat.entity.po.User;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.service.ChatService;
import com.djy.wechat.service.FriendService;
import com.djy.wechat.service.UserService;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.constants.Status;
import com.djy.wechat.service.impl.ChatServiceImpl;
import com.djy.wechat.service.impl.FriendServiceImpl;
import com.djy.wechat.service.impl.UserServiceImpl;
import com.djy.wechat.util.BeanUtils;
import com.djy.wechat.util.Constants;
import com.djy.wechat.util.MailUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;

import static com.djy.wechat.util.BeanUtils.jsonToJavaObject;
import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @program wechat
 * @description 用于处理用户相关业务流程
 */
@ActionProvider(path = "/user")
public class UserProvider extends Provider {
    //免登录时间
    private final int AUTO_LOGIN_AGE = 60 * 60 * 24 *7;
    private final String AUTO_LOGIN_PATH = "/";
    private final UserService userService = (UserService) new ServiceProxyFactory().getProxyInstance(new UserServiceImpl());
    private final ChatService chatService = (ChatService) new ServiceProxyFactory().getProxyInstance(new ChatServiceImpl());
    private final FriendService friendService = (FriendService) new ServiceProxyFactory().getProxyInstance(new FriendServiceImpl());

    /**
     * 提供用户注册的业务流程
     *
     * @name register
     */
    @Action(method = RequestMethod.REGISTER_DO)
    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        ServiceResult result;
        // 检查用户注册信息
        result = userService.checkRegister(user,req);
        if (Status.ERROR.equals(result.getStatus())) {
            req.setAttribute("message",result.getMessage());
            req.setAttribute("data",result.getData());
            req.getRequestDispatcher(WebPage.REGISTER_JSP.toString()).forward(req,resp);
            return;
        }
        //插入用户
        result = userService.insertUser(user);
        if (Status.ERROR.equals(result.getStatus())) {
            req.setAttribute("message",result.getMessage());
            req.setAttribute("data",result.getData());
            req.getRequestDispatcher(WebPage.REGISTER_JSP.toString()).forward(req,resp);
        } else {
            //注册成功后将用户添加到聊天总群中
            user = (User) result.getData();
            //与系统账号加好友
            addToGDUTChat(user);
            req.setAttribute("message",result.getMessage());
            req.setAttribute("data",result.getData());
            req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req,resp);
        }
    }


    /**
     * 提供用户登录的业务流程
     *
     * @name login
     */
    @Action(method = RequestMethod.LOGIN_DO)
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        ServiceResult result;
        if (user == null) {
            result = new ServiceResult(Status.ERROR, ServiceMessage.PARAMETER_NOT_ENOUGHT.message, null);
            req.setAttribute("message",result.getMessage());
            req.setAttribute("data",result.getData());
            req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req,resp);
            return;
        }
        HttpSession session = req.getSession(false);
        //检查用户是否已经建立会话并且已经具有登录信息
        if (session == null || session.getAttribute("login") == null) {
            //检查是不是游客登录，游客登录的话先创建个游客账号然后登录
            if ("visitor".equals(user.getEmail())) {
                result = userService.visitorLogin();
                if (Status.ERROR.equals(result.getStatus())) {
                    req.setAttribute("message",result.getMessage());
                    req.setAttribute("data",result.getData());
                    req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req,resp);
                    return;
                }
                User visitor = (User) result.getData();
                //与系统账号加好友
                addToGDUTChat(visitor);
            } else {
                //如果是用户登录，检查密码是否正确
                result = userService.checkPassword(user);
                if (Status.ERROR.equals(result.getStatus())) {
                    req.setAttribute("message",result.getMessage());
                    req.setAttribute("data",result.getData());
                    req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req,resp);
                    return;
                } else {
                    //检查图片验证码是否正确
                    boolean isSame = validateCode(req);
                    if (!isSame){
                        req.setAttribute("message","验证码输入不正确");
                        req.setAttribute("data","");
                        req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req,resp);
                        return;
                    }
                    //校验密码成功时，给会话中添加用户信息
                    result = userService.getUser(user.getId());
                    user = (User) result.getData();
                    //如果设置自动登录，则添加cookie
                    if (req.getParameter("auto_login")!=null) {
                        setAutoLoginCookie(resp,req,  String.valueOf(user.getId()));
                    }

                }
            }

        } else {
            //先从session获取用户信息，再更新用户信息到会话中
            user = (User) session.getAttribute("login");
            result = userService.getUser(user.getId());
        }
        req.getSession(true).setAttribute("login", result.getData());
        if (Constants.ADMIN_ROLE.equals(user.getRoleName())) {
            req.getRequestDispatcher(WebPage.ADMIN_JSP.toString()).forward(req,resp);
        }else {
            req.getRequestDispatcher(WebPage.INDEX_JSP.toString()).forward(req,resp);
        }
    }

    private boolean validateCode(HttpServletRequest request) {
        // 存在session中的图片验证码
        String sessionCode = (String)request.getSession().getAttribute("code");
        // 用户提交的图片验证码
        String userCode = request.getParameter("code");

        if (userCode == null || "".equals(userCode.trim())) {
            return false;
        }
        return userCode.equalsIgnoreCase(sessionCode);
    }

    /**
     * 提供获取用户个人信息的业务流程
     *
     * @name get
     */
    @Action(method = RequestMethod.GET_DO)
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        ServiceResult result;
        //获取用户数据
        result = userService.getUser(user.getId());
        if (Status.ERROR.equals(result.getStatus())) {
            returnJsonObject(resp, result);
        } else {
            //获取数据成功时的处理
            resp.getWriter().write(result.getMessage());
        }
    }


    /**
     * 提供获取用户个人信息的业务流程
     *
     * @name logout
     */
    @Action(method = RequestMethod.LOGOUT_DO)
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session != null) {
            session.invalidate();
        }
        returnJsonObject(resp, new ServiceResult(Status.SUCCESS, ServiceMessage.LOGOUT_SUCCESS.message, null));
    }


    /**
     * 提供用户更新个人信息的业务流程
     *
     * @name update
     */
    @Action(method = RequestMethod.UPDATE_DO)
    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) jsonToJavaObject(req.getInputStream(), User.class);
        ServiceResult result;
        if (user != null && user.getWechatId() != null) {
            User oldUser = (User) userService.getUser(user.getId()).getData();
            if (!oldUser.getWechatId().equals(user.getWechatId())) {
                //如果请求要求修改微信名，先检查用户名（微信号）是否合法
                result = userService.checkWechatId(user.getWechatId());
                if (Status.ERROR.equals(result.getStatus())) {
                    returnJsonObject(resp, result);
                    return;
                }
            }
        }
        //更新用户数据
        result = userService.updateUser(user);
        returnJsonObject(resp, result);
    }

    /**
     * 提供用户更新密码的业务流程
     *
     * @name updatePwd
     */
    @Action(method = RequestMethod.UPDATEPASSWORD_DO)
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String oldPwd = req.getParameter("old_password");
        String newPwd = req.getParameter("new_password");
        String userId = req.getParameter("user_id");
        ServiceResult result;
        //更新用户数据
        result = userService.updatePwd(oldPwd, newPwd, new BigInteger(userId));
        returnJsonObject(resp, result);
    }

    /**
     * 忘记密码时重置密码
     *
     * @name updatedPwd
     */
    @Action(method = RequestMethod.UPDATEDPASSWORD_DO)
    public void updatedPwd(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String userEmail = req.getParameter("email");
        String newPassword = req.getParameter("password");
        ServiceResult result;
        //更新用户密码
        result = userService.updatedPwd(userEmail,newPassword);
        returnJsonObject(resp, result);
        if (Status.ERROR.equals(result.getStatus())) {
            req.setAttribute("message",result.getMessage());
            req.setAttribute("data",result.getData());
            req.getRequestDispatcher(WebPage.REGISTER_JSP.toString()).forward(req,resp);
        } else {
            req.setAttribute("message",result.getMessage());
            req.setAttribute("data",result.getData());
            req.getRequestDispatcher(WebPage.LOGIN_JSP.toString()).forward(req,resp);
        }

    }

    /**
     * 提供搜索用户的服务
     *
     * @name list
     */
    @Action(method = RequestMethod.LIST_DO)
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        ServiceResult result;
        result = userService.listUserLikeName(user.getName());
        if (Status.ERROR.equals(result.getStatus())) {
            returnJsonObject(resp, result);
            return;
        }
        returnJsonObject(resp, result);
    }

    /**
     * 发送邮箱验证码
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    @Action(method = RequestMethod.SENDMAILCODE_DO)
    public void sendMailCode(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        ServiceResult result = new ServiceResult();
        String userEmail = req.getParameter("userEmail");
        try {
            MailUtils mailUtils = new MailUtils();
            mailUtils.send(userEmail);
            String mailCode = mailUtils.getMailCode();
            req.getSession().setAttribute("mailCode", mailCode);
        }catch (Exception e) {
            result.setStatus(Status.ERROR);
            result.setMessage(ServiceMessage.SEND_MAIL_ERROR.message);
            returnJsonObject(resp, result);
            return;
        }
        result.setStatus(Status.SUCCESS);
        result.setMessage(ServiceMessage.OPERATE_SUCCESS.message);
        returnJsonObject(resp, result);
    }

    /**
     * 提供自动登录的服务
     *
     * @name list
     */
    public void autoLogin(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equalsIgnoreCase(cookie.getName())) {
                    ServiceResult result = userService.getUser(cookie.getValue());
                    if (Status.SUCCESS.equals(result.getStatus())) {
                        HttpSession session = req.getSession();
                        session.setAttribute("login", result.getData());
                        return;
                    }
                }
            }
        }
    }


    /**
     * 设置用于自动登录的cookie
     * @param userId 用户id
     * @name setAutoLoginCookie
     */
    private void setAutoLoginCookie(HttpServletResponse resp,HttpServletRequest req, String userId) {
        Cookie cookie = new Cookie("user_id", userId);
        cookie.setMaxAge(AUTO_LOGIN_AGE);
        cookie.setPath(req.getContextPath());
        resp.addCookie(cookie);
    }

    /**
     * 将一个用户添加到与系统的会话中
     * @param user 用户
     * @name addToSystemChat
     */
    private void addToGDUTChat(User user) {
        Friend friend = new Friend();
        //系统添加用户账号为好友
        friend.setUserId(UserServiceImpl.gdutId);
        friend.setFriendId(user.getId());
        friendService.addFriend(friend);
        //用户添加系统账号为好友
        friend.setAlias(null);
        friend.setUserId(user.getId());
        friend.setFriendId(UserServiceImpl.gdutId);
        friendService.addFriend(friend);
        //将用户和系统账号添加到同一个聊天中
        chatService.createFriendChat(friend);
        //插入一条消息
        Message message = new Message();
        message.setContent("早上好，你今天安康打卡了吗^_^");
    }

}
