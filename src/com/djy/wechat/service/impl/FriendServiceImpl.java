package com.djy.wechat.service.impl;

import com.djy.wechat.dao.*;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Friend;
import com.djy.wechat.entity.po.Message;
import com.djy.wechat.entity.po.Report;
import com.djy.wechat.entity.po.User;
import com.djy.wechat.service.FriendService;
import com.djy.wechat.service.constants.MessageType;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.constants.Status;
import com.djy.wechat.util.Constants;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static com.djy.wechat.service.constants.ServiceMessage.*;

/**
 * @description 负责提供好友相关服务
 */
public class FriendServiceImpl implements FriendService {
    private final FriendDao friendDao = (FriendDao) DaoProxyFactory.getInstance().getProxyInstance(FriendDao.class);
    private final UserDao userDao = (UserDao) DaoProxyFactory.getInstance().getProxyInstance(UserDao.class);
    private final ChatDao chatDao = (ChatDao) DaoProxyFactory.getInstance().getProxyInstance(ChatDao.class);
    private final MessageDao messageDao = (MessageDao)DaoProxyFactory.getInstance().getProxyInstance(MessageDao.class);
    private final ReportDao reportDao = (ReportDao) DaoProxyFactory.getInstance().getProxyInstance(ReportDao.class);

    /**
     * 添加好友关系
     * @param friend 要添加的好友关系
     * @return
     * @name addFriend
     */
    @Override
    synchronized public ServiceResult addFriend(Friend friend) {
        if (friend == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, friend);
        }
        //检查是否是2个用户
        if (friend.getFriendId().equals(friend.getUserId())) {
            return new ServiceResult(Status.ERROR, CANNOT_ADD_YOURSELF.message, friend);
        }
        //检查是否已经是好友
        if (isFriend(friend)) {
            return new ServiceResult(Status.ERROR, ALREADY_FRIEND.message, friend);
        }
        //用于发送好友通知
        Message message = new Message();
        try {
            //检查是否添加过好友，不可重复添加
            if (friendDao.getFriendByUIDAndFriendId(friend.getUserId(), friend.getFriendId()) != null) {
                return new ServiceResult(Status.ERROR, ALREADY_ADD_FRIEND.message, friend);
            }
            //检查好友是否存在
            if (userDao.getUserById(friend.getFriendId()) == null) {
                return new ServiceResult(Status.ERROR, FRIEND_NOT_EXIST.message, friend);
            }
            //检查用户是否存在
            User user = userDao.getUserById(friend.getUserId());
            if (user == null) {
                return new ServiceResult(Status.ERROR, ACCOUNT_NOT_FOUND.message, friend);
            }
            //设置好友默认备注为好友昵称
            if (friend.getAlias() == null) {
                User friendUser = userDao.getUserById(friend.getFriendId());
                friend.setAlias(friendUser.getName());
            }
            //添加好友
            if (friendDao.insert(friend) != 1) {
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
            }
            //添加好友后如果已经双向加好友，则不用发申请，而是把之前的申请消息删除
            if(isFriend(friend)){
                //查询自己与系统建立的聊天,用于查找发送好友申请那条消息并删除
                message.setSenderId(friend.getFriendId());
                Friend systemFriend = friendDao.getFriendByUIDAndFriendId(friend.getUserId(),UserServiceImpl.gdutId);
                if(systemFriend==null){
                    return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
                }
                message.setChatId(systemFriend.getChatId());
                messageDao.delete(message);
            }else {
                //如果对方是普通用户，需要发送加好友通知
                if (!UserServiceImpl.gdutId.equals(friend.getFriendId()) && !UserServiceImpl.gdutId.equals(friend.getUserId())) {
                    //获取好友与系统的聊天，用于推送通知
                    Friend systemFriend = friendDao.getFriendByUIDAndFriendId(UserServiceImpl.gdutId, friend.getFriendId());
                    //生成一条加好友通知消息
                    message.setChatId(systemFriend.getChatId());
                    message.setSenderId(user.getId());
                    message.setContent("好友申请");
                    message.setType(MessageType.FRIEND.toString());
                    message.setTime(new Timestamp(System.currentTimeMillis()));
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
        }
        return new ServiceResult(Status.SUCCESS, ADD_FRIEND_SUCCESS.message, message);
    }

    /**
     * 返回一个用户的好友列表
     * @param userId 用户id
     * @return
     * @name listFriend
     */
    @Override
    public ServiceResult listFriend(Object userId) {
        if (userId == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, null);
        }
        List<Friend> friendList = new LinkedList<>();
        try {
            List<Friend> list = friendDao.listByUserId(userId);
            //只返回双向添加为好友的关系
            for (Friend friend : list) {
                if (isFriend(friend)) {
                    // 查询user_id举报friend_id的记录
                    Report report = reportDao.getReport(userId, friend.getFriendId());
                    if (report != null) {
                        // 举报记录不为空,说明已被举报
                        friend.setFriendReported("Y");
                    } else {
                        friend.setFriendReported("N");
                    }
                    friendList.add(friend);
                }
            }
            if (friendList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_FRIEND_NOW.message, userId);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, userId);
        }
        return new ServiceResult(Status.SUCCESS, null, friendList);
    }

    /**
     * 更新好友信息
     * @param friend 朋友
     * @name updateFriend
     */
    @Override
    public ServiceResult updateFriend(Friend friend) {
        if (friend == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, null);
        }
        try {
            //检查成员是否存在
            if (friendDao.getFriendById(friend.getId()) == null) {
                return new ServiceResult(Status.ERROR, FRIEND_NOT_EXIST.message, friend);
            }
            if (friendDao.update(friend) != 1) {
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, friend);
        }
        return new ServiceResult(Status.SUCCESS, UPDATE_FRIEND_SUCCESS.message, friend);
    }

    /**
     * 移除好友
     * @param friend 要移除的好友关系
     * @name removeFriend
     */
    @Override
    synchronized public ServiceResult removeFriend(Friend friend) {
        if (friend == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, friend);
        }

        try {
            //检查成员是否存在
            if (friendDao.getFriendByUIDAndFriendId(friend.getUserId(), friend.getFriendId()) != null) {
                //将该成员从聊天中移除
                if (friendDao.delete(friend) != 1) {
                    return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
                }
                //将对方的的朋友记录也删除
                friend = friendDao.getFriendByUIDAndFriendId(friend.getFriendId(), friend.getUserId());
                if (friendDao.delete(friend) != 1) {
                    return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, friend);
        }
        return new ServiceResult(Status.SUCCESS, ServiceMessage.DELETE_FRIEND_SUCCESS.message, friend);
    }

    /**
     * 判断是否存在一条这样的朋友记录
     * @param friend 判断该朋友关系是否是双向的
     * @name isFriend
     */
    @Override
    public boolean isFriend(Friend friend) {

        try {
            Friend reverseFriend = friendDao.getFriendByUIDAndFriendId(friend.getFriendId(), friend.getUserId());
            friend = friendDao.getFriendByUIDAndFriendId(friend.getUserId(), friend.getFriendId());
            if (reverseFriend == null || friend == null) {
                //反向查询没有结果则是单向的
                return false;
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 通过用户id和朋友id查询朋友关系
     * @param uid      用户id
     * @param friendId 朋友id
     * @name getByUidAndFriendId
     */
    @Override
    public Friend getByUidAndFriendId(Object uid, Object friendId) {
        Friend friend = null;
        try {
            friend = friendDao.getFriendByUIDAndFriendId(uid, friendId);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return friend;
    }

    /**
     * 拉黑好友
     * @param friend
     * @return
     */
    @Override
    synchronized public ServiceResult pullInBlacklist(Friend friend) {
        if (friend == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, friend);
        }
        try {
            //检查成员是否存在
            if (friendDao.getFriendByUIDAndFriendId(friend.getUserId(), friend.getFriendId()) != null) {
                friend.setFriendInBlack(Constants.YES);
                if (friendDao.update(friend) != 1) {
                    return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, friend);
        }
        return new ServiceResult(Status.SUCCESS, BLACKLIST_FRIEND_SUCCESS.message, friend);
    }

    /**
     * 举报好友
     * @param friend
     * @param reportCont
     * @return
     */
    @Override
    synchronized public ServiceResult reportFriend(Friend friend, String reportCont) {
        if (friend == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, friend);
        }
        if (reportCont == null || "".equals(reportCont.trim())) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, reportCont);
        }
        try {
            //检查成员是否存在
            if (friendDao.getFriendByUIDAndFriendId(friend.getUserId(), friend.getFriendId()) != null) {
                Report report = new Report();
                report.setUserId(friend.getUserId());
                report.setFriendId(friend.getFriendId());
                report.setReportCont(reportCont);
                report.setStatus(0);
                if (reportDao.insert(report) != 1) {
                    return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, friend);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, friend);
        }
        return new ServiceResult(Status.SUCCESS, REPORT_FRIEND_SUCCESS.message, friend);
    }
}
