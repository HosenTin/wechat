package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.*;

import java.math.BigInteger;

/**
 * @description 提供聊天相关的服务
 */
public interface ChatService {

    /**
     * 创建一个聊天,如果是群聊，必须指定外部唯一标识（群号），如果是私聊则自动使用uuid作为唯一标识
     * @param isGroupChat 是否为群聊
     * @param chat        要创建的聊天对象
     * @return 返回传入的聊天对象
     * @name createChat
     */
    ServiceResult createChat(Chat chat, boolean isGroupChat);

    /**
     * 给已给好友关系创建一个聊天关系，并把两者加入到此聊天，并更新好友关系上的聊天关系id
     * @param friend 好友关系
     * @name createFriendChat
     */
    ServiceResult createFriendChat(Friend friend);

    /**
     * 把用户添加到聊天中
     * @param members 要添加的成员对象
     * @return 返回传入的成员对象
     * @name joinChat
     */
    ServiceResult joinChat(Member[] members);

    /**
     * 通过群号将一个用户添加到群聊中
     * @param userId 用户id
     * @param number 群号
     * @param apply  加群申请
     * @name joinChatByNumber
     */
    ServiceResult joinChatByNumber(BigInteger userId, String number, String apply);

    /**
     * 把成员从聊天中移除，如果该成员是聊天的最后一个成员
     * 就将该聊天一并删除
     * @param members 要移除的成员对象
     * @return 返回移除的成员对象
     * @name quitChat
     */
    ServiceResult quitChat(Member members);

    /**
     * 将一个成员从聊天中移除
     * @param memberId 成员id
     * @name removeFromChat
     */
    ServiceResult removeFromChat(BigInteger memberId);

    /**
     * 返回一个用户的所有聊天
     * @param user 用户对象
     * @return 该用户的所有聊天
     * @name listChat
     */
    ServiceResult listChat(User user);

    /**
     * 通过用户id和群号获取一个聊天
     * @param userId 用户id
     * @param number 聊天id
     * @name getChat
     */
    ServiceResult getChat(String number, BigInteger userId);

    /**
     * 删除一个聊天
     * @param chat
     * @return
     * @name removeChat
     */
    void removeChat(Chat chat);

    /**
     * 查询一个聊天中所有成员的信息
     * @param chatId 聊天id
     * @name listMember
     */
    ServiceResult listMember(BigInteger chatId);

    /**
     * 获取打招呼消息
     * @param member 成员
     * @name getHelloMessage
     */
    Message getHelloMessage(Member member);

    /**
     * 通过用户id检测是否为群主
     * @name isOwner
     * @param memberId 成员
     * @param userId 操作用户id
     */
    boolean isOwner(BigInteger memberId, BigInteger userId);

}
