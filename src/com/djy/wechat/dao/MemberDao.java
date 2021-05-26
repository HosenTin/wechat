package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.entity.po.Member;

import java.math.BigInteger;
import java.util.List;

/**
 * @description 用于user_chat表的CRUD
 */
public interface MemberDao extends BaseDao {
    String TABLE = "member";
    String ALL_FIELD = "user_id,chat_id,group_alias,apply," + BASE_FIELD;

    /**
     * 通过成员id查询一个成员
     * @param id 成员id
     * @return 成员对象
     * @name getMemberById
     */
    @Result(entity = Member.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where id = ? ")
    Member getMemberById(Object id);

    /**
     * 通过用户id和聊天id查询一个成员
     * @param userId 用户id
     * @param chatId 聊天id
     * @return 成员对象
     * @name getMemberByUIdAndChatId
     */
    @Result(entity = Member.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where user_id = ? and chat_id = ? ")
    Member getMemberByUIdAndChatId(BigInteger userId, BigInteger chatId);


    /**
     * 返回一个聊天中的所有成员
     * @name listMemberByChatId
     * @param chatId 聊天id
     * @return
     */
    @Result(entity = Member.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where chat_id = ? ")
    List<Member> listMemberByChatId(Object chatId);

}
