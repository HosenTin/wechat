package com.djy.wechat.service.impl;

import com.djy.wechat.dao.*;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.exception.ServiceException;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.builder.MessageVOBuilder;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Member;
import com.djy.wechat.entity.po.Message;
import com.djy.wechat.entity.po.Record;
import com.djy.wechat.entity.po.User;
import com.djy.wechat.entity.vo.MessageVO;
import com.djy.wechat.service.MessageService;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.constants.Status;

import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static com.djy.wechat.service.constants.ServiceMessage.*;
import static com.djy.wechat.util.UUIDUtils.getUUID;

/**
 * @description 负责提供消息和聊天记录的服务
 */
public class MessageServiceImpl implements MessageService {


    private final ChatDao chatDao = (ChatDao) DaoProxyFactory.getInstance().getProxyInstance(ChatDao.class);
    private final MessageDao messageDao = (MessageDao) DaoProxyFactory.getInstance().getProxyInstance(MessageDao.class);
    private final RecordDao recordDao = (RecordDao) DaoProxyFactory.getInstance().getProxyInstance(RecordDao.class);
    private final MemberDao memberDao = (MemberDao) DaoProxyFactory.getInstance().getProxyInstance(MemberDao.class);
    private final UserDao userDao = (UserDao) DaoProxyFactory.getInstance().getProxyInstance(UserDao.class);

    /**
     * 将一条消息存入数据库，同时给聊天中的所有成员生成一份聊天记录
     * @param message 要插入的消息
     * @name insertMessage
     */
    @Override
    public void insertMessage(Message message) {
        try {
            //将消息插入数据库
            if (messageDao.insert(message) != 1) {
                throw new ServiceException(DATABASE_ERROR.message + message.toString());
            }
            //检查是否有时间参数
            if(message.getTime()==null){
                throw new ServiceException(MISSING_TIME.message + message.toString());
            }
            if(message.getType()==null){
                throw new ServiceException(MISSING_TYPE.message + message.toString());
            }
            message = messageDao.getMessageBySenderIdAndChatIdAndTime(message.getSenderId(), message.getChatId(), message.getTime());
            //对每条消息给聊天中所有成员产生消息记录
            //加载用户所在的所有聊天中的所有成员,给所有成员插入记录
            List<Member> memberList = memberDao.listMemberByChatId(message.getChatId());
            if (memberList != null && memberList.size() > 0) {
                for (Member member : memberList) {
                    Record record = new Record();
                    record.setMessageId(message.getId());
                    record.setUserId(member.getUserId());
                    if (recordDao.insert(record) != 1) {
                        throw new ServiceException(DATABASE_ERROR.message + record.toString());
                    }
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    /**
     * 获取一个用户在一个聊天中的所有消息记录，不包括被删除的消息记录
     * @param userId 用户id
     * @param chatId 聊天id
     * @param page   页数
     */
    @Override
    public ServiceResult listAllMessage(Object userId, Object chatId, int page) {
        int limit = 200;
        int offset = (page - 1) * limit;
        //检查页数
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        List<MessageVO> messageVOList = new LinkedList<>();

        //数据判空
        if (userId == null || chatId == null) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PARAMETER_NOT_ENOUGHT.message, null);
        }
        try {
            User user = userDao.getUserById(userId);
            //检查用户是否存在
            if (user == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.ACCOUNT_NOT_FOUND.message, null);
            }
            //查询数据库
            List<Message> messageList = messageDao.listMessageByUserIdAndChatIdDesc(userId, chatId, limit, offset);
            //检查数据是否存在
            if (messageList == null || messageList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, null, messageList);
            }
            toMessageVOObject(messageVOList, messageList);
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, chatId);

        }
        return new ServiceResult(Status.SUCCESS, null, messageVOList);
    }

    /**
     * 获取一个用户的所有未读的消息
     * @param userId 用户id
     * @param page   页数
     */
    @Override
    public ServiceResult listAllUnreadMessage(Object userId, int page) {
        if (userId == null) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PARAMETER_NOT_ENOUGHT.message, null);
        }
        List<MessageVO> messageVOList = new LinkedList<>();
        //一次最多获取2000条未读消息
        int limit = 2000;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        try {
            User user = userDao.getUserById(userId);
            //检查用户是否存在
            if (user == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.ACCOUNT_NOT_FOUND.message, null);
            }
            List<Message> messageList = messageDao.listUnreadMessageByUserId(userId, limit, offset);
            //检查数据是否存在
            if (messageList == null || messageList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, null, messageList);
            }
            //使用记录和用户信息建造视图层消息实体
            toMessageVOObject(messageVOList, messageList);
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, null);
        }
        return new ServiceResult(Status.SUCCESS, null, messageVOList);
    }

    /**
     * 获取一个用户在一个聊天中的所有未读的消息
     * @param userId 用户id
     * @param chatId 聊天id
     * @param page   页数
     * @name listUnreadMessage
     */
    @Override
    public ServiceResult listUnreadMessage(Object userId, Object chatId, int page) {
        if (userId == null || chatId == null) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PARAMETER_NOT_ENOUGHT.message, null);
        }
        List<MessageVO> messageVOList = new LinkedList<>();
        List<Message> list = null;
        int limit = 1000;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        try {
            User user = userDao.getUserById(userId);
            //检查用户是否存在
            if (user == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.ACCOUNT_NOT_FOUND.message, null);
            }
            List<Message> messageList = messageDao.listUnreadMessage(userId, chatId, limit, offset);
            //检查数据是否存在
            if (messageList == null || messageList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_RECORD.message, messageList);
            }
            //使用记录和用户信息建造视图层消息实体
            toMessageVOObject(messageVOList, messageList);
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, null);
        }
        return new ServiceResult(Status.SUCCESS, null, messageVOList);
    }

    /**
     * 删除一个用户在一个聊天中的所有记录
     *
     * @param userId 用户id
     * @param chatId 要移除的消息记录的聊天id
     * @name removeAllMessage
     */
    @Override
    public ServiceResult removeAllMessage(BigInteger userId, BigInteger chatId) {
        if (userId == null | chatId == null) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PARAMETER_NOT_ENOUGHT.message, null);
        }
        try {
            if (chatDao.getChatById(chatId) == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.CHAT_NOT_FOUND.message, null);
            }
            if (userDao.getUserById(userId) == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.ACCOUNT_NOT_FOUND.message, null);
            }
            //将记录移除
            recordDao.deleteAllRecordInChat(userId, chatId);
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, chatId);
        }
        return new ServiceResult(Status.SUCCESS, ServiceMessage.OPERATE_SUCCESS.message, chatId);
    }
    /**
     * 将一个用户在一个聊天中收到的消息记录设置为已读
     * @param userId 用户id
     * @param chatId 聊天id
     * @name setAlreadyRead
     */
    @Override
    public void setAlreadyRead(Object userId, Object chatId) {
        try {
            recordDao.updateStatusInChat(1, userId, chatId);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把一个装有数据库表实体类对象的集合和一个用户的信息，转化成一个视图层消息集合
     * @param messageVOList 视图层对象集合
     * @param messageList   持久化层对象集合
     * @name toMessageVOObject
     */
    public void toMessageVOObject(List<MessageVO> messageVOList, List<Message> messageList) {
        //使用记录和用户信息建造视图层消息实体
        for (Message message : messageList) {
            User sender = userDao.getUserById(message.getSenderId());
            if (sender == null) {
                throw new ServiceException("消息的发送者不存在");
            }
            MessageVO messageVo = new MessageVOBuilder().setSenderId(message.getSenderId())
                    .setSenderName(sender.getName()).setChatId(message.getChatId())
                    .setContent(message.getContent()).setSenderPhoto(sender.getPhoto())
                    .setTime(message.getTime()).setType(message.getType()).build();
            messageVOList.add(messageVo);
        }
    }

}
