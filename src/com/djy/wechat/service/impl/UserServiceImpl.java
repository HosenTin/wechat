package com.djy.wechat.service.impl;

import com.djy.wechat.dao.UserDao;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.User;
import com.djy.wechat.service.UserService;
import com.djy.wechat.service.constants.Status;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static com.djy.wechat.service.constants.ServiceMessage.*;
import static com.djy.wechat.util.Md5Utils.getDigest;
import static com.djy.wechat.util.StringUtils.toLikeSql;
import static com.djy.wechat.util.UUIDUtils.getUUID;

/**
 * @program wechat
 * @description 负责提供用户相关服务
 */
public class UserServiceImpl implements UserService {
    /**
     * 游客使用的邮箱
     */
    public static final String VISITOR_EMAIL = "visitor@wechat.com";

    private final UserDao userDao = (UserDao) DaoProxyFactory.getInstance().getProxyInstance(UserDao.class);
    /**
     * 系统账号id
     */
    public static final BigInteger gdutId = BigInteger.valueOf(0);
    /**
     * 检查注册用户的信息是否有效
     * @param user 用户对象
     * @param req
     * @return 返回传入时的对象
     */
    @Override
    public ServiceResult checkRegister(User user, HttpServletRequest req) {
        if(user==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        try {
            //防止插入id
            user.setId(null);
            //检查邮箱格式
            if (!isValidEmail(user.getEmail())) {
                return new ServiceResult(Status.ERROR, EMAIL_FORMAT_INCORRECT.message, user);
            }
            //检查邮箱验证码
            if (!validateEmailCode(req)) {
                return new ServiceResult(Status.ERROR, EMAIL_CODE_INCORRECT.message, user);
            }
            //检查邮箱是否已被注册
            if (userDao.getUserByEmail(user.getEmail()) != null) {
                return new ServiceResult(Status.ERROR, EMAIL_ALREADY_USED.message, user);
            }
            //检查密码是否合法
            if (!isValidPassword(user.getPassword())) {
                return new ServiceResult(Status.ERROR, INVALID_PASSWORD.message, user);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);
        }
        return new ServiceResult(Status.SUCCESS, REGISTER_INFO_VALID.message, user);
    }

    private boolean validateEmailCode(HttpServletRequest request) {
        //存在session中的mailCode：UserProvider的sendMailCode
        String sessionMailCode = (String)request.getSession().getAttribute("mailCode");
        //用户输入的邮箱验证码：register.jsp的id="mailCode"
        String userMailCode = request.getParameter("mailCode");

        if (userMailCode == null || "".equals(userMailCode.trim())) {
            return false;
        }
        return userMailCode.equals(sessionMailCode);
    }

    /**
     * 添加一个用户账号
     * @param user 用户对象
     * @return 返回传入的用户的对象
     */
    @Override
    public ServiceResult insertUser(User user) {

        if (user == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, null);
        }
        try {
            //插入前对用户的密码进行加密
            user.setPassword(getDigest(user.getPassword()));
            //给用户设置默认昵称
            if (user.getName() == null) {
                user.setName("微信用户");
            }
            if (userDao.insert(user) != 1) {
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);
            }
            user = userDao.getUserByEmail(user.getEmail());
            //插入后销毁用户对象中的密码数据
            user.setPassword(null);
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);
        }
        return new ServiceResult(Status.SUCCESS, REGISTER_SUCCESS.message, user);
    }

    /**
     * 校验用户的密码
     * @param user 用户对象
     * @return 返回传入的用户对象
     */
    @Override
    public ServiceResult checkPassword(User user) {
        if(user==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        User realUser;
        try {
            realUser = userDao.checkPassword(user.getEmail(),user.getRoleName());
            //检查账号是否存在
            if (realUser == null) {
                return new ServiceResult(Status.ERROR, ACCOUNT_NOT_FOUND.message, user);
            }
            //检查密码是否正确
            if (user.getPassword() == null || !realUser.getPassword().equals(getDigest(user.getPassword()))) {
                return new ServiceResult(Status.ERROR, PASSWORD_INCORRECT.message, user);
            }
            //成功后返回该用户的id信息
            user.setId(realUser.getId());
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);

        }
        return new ServiceResult(Status.SUCCESS, LOGIN_SUCCESS.message, user);
    }

    /**
     * 校验微信号，是否合法或已被占用
     * @param wechatId 微信号
     * @return 返回传入的用户名
     */
    @Override
    public ServiceResult checkWechatId(String wechatId) {
        if(wechatId==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        try {
            //检查是否合法
            if (!isValidWechatId(wechatId)) {
                return new ServiceResult(Status.ERROR, WECHAT_ID_INVALID.message, wechatId);
            }
            //检查是否重复
            if (userDao.getUserByWechatId(wechatId) != null) {
                return new ServiceResult(Status.ERROR, WECHAT_ID_USED.message, wechatId);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, wechatId);
        }
        return new ServiceResult(Status.SUCCESS, WECHAT_ID_VALID.message, wechatId);
    }

    /**
     * 更新用户的个人密码
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param userId 用户id
     * @return 返回传入的用户对象，如果由密码信息/邮箱信息，将被清空
     */
    @Override
    public ServiceResult updatePwd(String oldPwd, String newPwd, BigInteger userId) {
        if(oldPwd==null||newPwd==null||userId==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        try {
            User user = userDao.getUserById(userId);
            //检查账号是否存在
            if (user == null) {
                return new ServiceResult(Status.ERROR, ACCOUNT_NOT_FOUND.message, userId);
            }
            //检查旧密码是否正确
            if (!user.getPassword().equals(getDigest(oldPwd))) {
                return new ServiceResult(Status.ERROR, PASSWORD_INCORRECT.message, user);
            }
            //检查新密码是否合法
            if(!isValidPassword(newPwd)){
                return new ServiceResult(Status.ERROR, INVALID_PASSWORD.message, user);
            }
            //更新密码
            user.setPassword(getDigest(newPwd));
            if(userDao.update(user)!=1){
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);
            }
        }catch (DaoException e){
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message,userId);
        }
        return new ServiceResult(Status.SUCCESS,UPDATE_PASSWORD_SUCCESS.message+newPwd,userId);
    }

    /**
     * 通过用户id获取用户个人信息
     * @param id 用户id
     * @return 返回用户的个人信息
     */
    @Override
    public ServiceResult getUser(Object id) {
        if(id==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        User user = null;
        try {
            user = userDao.getUserById(id);
            if (user == null) {
                return new ServiceResult(Status.ERROR, NO_USER_INFO.message, user);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);
        }
        return new ServiceResult(Status.SUCCESS, GET_INFO_SUCCESS.message, user);
    }

    /**
     * 更新用户的个人信息,不包括密码，邮箱
     * @param user 用户对象
     * @return 返回传入的用户对象，如果由密码信息/邮箱信息，将被清空
     */
    @Override
    public ServiceResult updateUser(User user) {
        if(user==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        try {
            //阻止更新用户密码
            user.setPassword(null);
            //阻止更新邮箱
            user.setEmail(null);
            if (userDao.update(user) != 1) {
                return new ServiceResult(Status.ERROR, UPDATE_USER_FAILED.message, user);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, user);
        }
        return new ServiceResult(Status.SUCCESS, UPDATE_INFO_SUCCESS.message, user);
    }

    /**
     * 返回昵称与传入参数相似的用户列表
     * @param name 用户昵称
     * @name listUserLikeName
     */
    @Override
    public ServiceResult listUserLikeName(String name) {
        List<User> list = new LinkedList<>();
        try {
            //查找内容判空
            if (name == null || name.trim().isEmpty()) {
                return new ServiceResult(Status.ERROR, "关键词" + NOT_NULL.message, list);
            }
            //将关键词转换成模糊查询
            String[] likeSql1 = toLikeSql(name);
            for (String sql : likeSql1) {
                list.addAll(userDao.listLikeName(sql));
            }
            if (list.size() == 0) {
                return new ServiceResult(Status.ERROR, NO_SUCH_USER.message, list);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, list);
        }
        return new ServiceResult(Status.SUCCESS, OPERATE_SUCCESS.message, list);
    }

    /**
     * 创建一个游客账号，并自动通过登录
     * @name visitorLogin
     */
    @Override
    public ServiceResult visitorLogin() {
        User visitor = new User();
        //游客统一使用此邮箱，便于将来批量删除游客数据
        visitor.setEmail(VISITOR_EMAIL);
        visitor.setName("游客");
        visitor.setWechatId(getUUID());
        try {
            if(userDao.insert(visitor)!=1){
                return new ServiceResult(Status.ERROR,DATABASE_ERROR.message,visitor);
            }
            visitor = userDao.getUserByWechatId(visitor.getWechatId());
            if(visitor==null){
                return new ServiceResult(Status.ERROR,DATABASE_ERROR.message,null);
            }
        }catch (DaoException e){
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message,visitor);
        }
        return new ServiceResult(Status.SUCCESS, LOGIN_SUCCESS.message,visitor);
    }

    /**
     * 忘记密码时重置密码
     * @param userEmail
     * @param newPassword
     * @return
     */
    @Override
    public ServiceResult updatedPwd(String userEmail,String newPassword) {
        if(userEmail==null||newPassword==null){
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message,null);
        }
        try {
            //查询出该邮箱的用户
            User user = userDao.getUserByEmail(userEmail);
            //检查邮箱是否存在
            if (user == null) {
                return new ServiceResult(Status.ERROR, ACCOUNT_NOT_FOUND.message,userEmail);
            }
            //检查新密码是否合法
            if(!isValidPassword(newPassword)){
                return new ServiceResult(Status.ERROR, INVALID_PASSWORD.message,user);
            }
            //更新密码
            user.setPassword(getDigest(newPassword));
            if(userDao.update(user)!=1){
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message,user);
            }
        }catch (DaoException e){
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message,userEmail);
        }
        return new ServiceResult(Status.SUCCESS,UPDATE_PASSWORD_SUCCESS.message+newPassword,userEmail);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        return email.matches(regex);
    }


    private boolean isValidWechatId(String wechatId) {
        if (wechatId == null || wechatId.trim().isEmpty()) {
            return false;
        }
        String regex = "[\\w_]{6,20}$";
        return wechatId.matches(regex);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        String regex = "[\\w_]{6,20}$";
        return password.matches(regex);
    }

    private boolean isValidPhoneNum(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        String regex = "0?(13|14|15|17|18|19)[0-9]{9}";
        return number.matches(regex);
    }

    private boolean isValidIdNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        String regex = "\\d{17}[\\d|x]|\\d{15}";
        return number.matches(regex);
    }

    private boolean isValidNickName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 20) {
            return false;
        }
        return true;
    }
}
