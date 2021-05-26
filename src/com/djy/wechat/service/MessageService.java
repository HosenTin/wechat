package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Message;

import java.math.BigInteger;

/**
 * @description 负责提供消息保存，聊天记录管理的服务
 */
public interface MessageService {

    /**
     * 将一条消息存入数据库，同时给聊天中的所有成员生成一份聊天记录
     * @param message 要插入的消息
     * @name insertMessage
     */
    void insertMessage(Message message);

    /**
     * 获取一个用户在一个聊天中的所有消息记录，不包括被删除的消息记录
     * @param userId 用户id
     * @param chatId 聊天id
     * @param page   页数
     */
    ServiceResult listAllMessage(Object userId, Object chatId, int page);

    /**
     * 获取一个用户在一个聊天中的所有未读的消息
     * @param userId 用户id
     * @param chatId 聊天id
     * @param page   页数
     * @name listUnreadMessage
     */
    ServiceResult listUnreadMessage(Object userId, Object chatId, int page);


    /**
     * 获取一个用户的所有未读的消息
     * @param userId 用户id
     * @param page   页数
     */
    ServiceResult listAllUnreadMessage(Object userId, int page);

    /**
     * 删除一个用户在一个聊天中的所有记录
     * @param userId 用户id
     * @param chatId 要移除的消息记录的聊天id
     * @name removeAllMessage
     */
    ServiceResult removeAllMessage(BigInteger userId, BigInteger chatId);

    /**
     * 将一个用户在一个聊天中收到的消息记录设置为已读
     * @param userId 用户id
     * @param chatId 聊天id
     * @name setAlreadyRead
     */
    void setAlreadyRead(Object userId, Object chatId);
}
