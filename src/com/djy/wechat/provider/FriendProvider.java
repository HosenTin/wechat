package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Chat;
import com.djy.wechat.entity.po.Friend;
import com.djy.wechat.entity.po.Message;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.server.ChatServer;
import com.djy.wechat.service.ChatService;
import com.djy.wechat.service.FriendService;
import com.djy.wechat.service.MessageService;
import com.djy.wechat.service.MomentService;
import com.djy.wechat.service.constants.MessageType;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.impl.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;

import static com.djy.wechat.service.constants.Status.ERROR;
import static com.djy.wechat.util.BeanUtils.jsonToJavaObject;
import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @description 负责好友相关业务流程
 */
@ActionProvider(path = "/friend")
public class FriendProvider extends Provider {
    private final FriendService friendService = (FriendService) new ServiceProxyFactory().getProxyInstance(new FriendServiceImpl());
    private final ChatService chatService = (ChatService) new ServiceProxyFactory().getProxyInstance(new ChatServiceImpl());
    private final MessageService messageService = (MessageService) new ServiceProxyFactory().getProxyInstance(new MessageServiceImpl());
    private final MomentService momentService = (MomentService)new ServiceProxyFactory().getProxyInstance(new MomentServiceImpl());

    /**
     * 提供添加好友的业务流程
     * @name addFriend
     */
    @Action(method = RequestMethod.ADD_DO)
    synchronized public void addFriend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Friend friend = (Friend) jsonToJavaObject(req.getInputStream(), Friend.class);
        ServiceResult result;
        //加好友
        result = friendService.addFriend(friend);
        if (ERROR.equals(result.getStatus())) {
            returnJsonObject(resp, result);
            return;
        }
        //添加好友后判断是否对方也将自己添加为好友，如果双向添加，则建立聊天关系，否则发送好友通知
        if (friendService.isFriend(friend)) {
            Chat chat = (Chat) chatService.createFriendChat(friend).getData();
            //发送打招呼消息
            Message message = new Message();
            message.setChatId(chat.getId());
            message.setSenderId(friend.getUserId());
            message.setContent(ServiceMessage.AGREE_FRIEND.message);
            message.setTime(new Timestamp(System.currentTimeMillis()));
            message.setType(MessageType.USER.toString());
            messageService.insertMessage(message);
            //初始化朋友圈(查询出当前用户所加这个好友的朋友圈)
            momentService.initNews(friend);
        } else {
            //生成的加好友通知，发送实时通知并存到数据库
            Message message = (Message) result.getData();
            ChatServer.sendNotify(message, friend.getFriendId());
            messageService.insertMessage(message);
        }
        returnJsonObject(resp, result);
    }

    /**
     * 提供获取好友列表的服务
     * @name listFriend
     */
    @Action(method = RequestMethod.LIST_DO)
    public void listFriend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //user_id: "${sessionScope.login.id}"
        String userId = req.getParameter("user_id");
        //定义ServiceResult类的result变量
        ServiceResult result;
        //result ：friendList的响应结果
        result = friendService.listFriend(new BigInteger(userId));
        //将结果写到浏览器
        returnJsonObject(resp, result);
    }

    /**
     * 提供获取好友列表的服务
     * @name listFriend
     */
    @Action(method = RequestMethod.UPDATE_DO)
    public void updateFriend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Friend friend = (Friend) jsonToJavaObject(req.getInputStream(), Friend.class);
        ServiceResult result;
        result = friendService.updateFriend(friend);
        returnJsonObject(resp, result);
    }

    /**
     * 提供删除好友的服务
     * @name deleteFriend
     */
    @Action(method = RequestMethod.DELETE_DO)
    synchronized public void deleteFriend(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String friendId = req.getParameter("friend_id");
        String userId = req.getParameter("user_id");

        ServiceResult result;
        //GDUT不可删除
        //删除好友之前将聊天关系移除,请求中的好友对象只含有userId和friendId，所以需要重新获取
        Friend friend = friendService.getByUidAndFriendId(new BigInteger(userId), new BigInteger(friendId));
        if (friend == null) {
            result = new ServiceResult();
            result.setMessage(ServiceMessage.FRIEND_NOT_EXIST.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        if (UserServiceImpl.gdutId.equals(friend.getFriendId())) {
            returnJsonObject(resp, new ServiceResult(ERROR, ServiceMessage.CANNOT_DELETE_SYSTEM.message, null));
            return;
        }
        //调用聊天服务将聊天关系解除
        Chat chat = new Chat();
        chat.setId(friend.getChatId());
        chatService.removeChat(chat);
        //解除好友关系
        result = friendService.removeFriend(friend);
        returnJsonObject(resp, result);
    }
    /**
     * 提供拉黑好友的服务
     */
    @Action(method = RequestMethod.BLACKLIST_DO)
    synchronized public void pullInBlacklist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String friendId = req.getParameter("friend_id");
        String userId = req.getParameter("user_id");

        ServiceResult result;
        //系统账号不可拉黑
        //拉黑好友之前先进行校验
        Friend friend = friendService.getByUidAndFriendId(new BigInteger(userId), new BigInteger(friendId));
        if (friend == null) {
            result = new ServiceResult();
            result.setMessage(ServiceMessage.FRIEND_NOT_EXIST.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        if (UserServiceImpl.gdutId.equals(friend.getFriendId())) {
            returnJsonObject(resp, new ServiceResult(ERROR, ServiceMessage.CANNOT_DELETE_SYSTEM.message, null));
            return;
        }
        //拉黑好友关系
        result = friendService.pullInBlacklist(friend);
        returnJsonObject(resp, result);
    }
    /**
     * 提供举报好友的服务
     */
    @Action(method = RequestMethod.REPORT_DO)
    synchronized public void reportFriend(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String friendId = req.getParameter("friend_id");
        String userId = req.getParameter("user_id");
        String reportCont = req.getParameter("report_cont");

        ServiceResult result;
        //系统账号不可举报
        //举报好友之前先进行校验
        Friend friend = friendService.getByUidAndFriendId(new BigInteger(userId), new BigInteger(friendId));
        if (friend == null) {
            result = new ServiceResult();
            result.setMessage(ServiceMessage.FRIEND_NOT_EXIST.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        if (UserServiceImpl.gdutId.equals(friend.getFriendId())) {
            returnJsonObject(resp, new ServiceResult(ERROR, ServiceMessage.CANNOT_DELETE_SYSTEM.message, null));
            return;
        }
        //拉黑好友关系
        result = friendService.reportFriend(friend,reportCont);
        returnJsonObject(resp, result);
    }
}
