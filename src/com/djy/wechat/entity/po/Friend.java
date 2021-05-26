package com.djy.wechat.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.djy.wechat.dao.annotation.Table;
import com.djy.wechat.entity.po.abs.BaseEntity;

import java.math.BigInteger;

/**
 * @program wechat
 * @description 朋友表
 */
@Table(name = "friend")
public class Friend extends BaseEntity {
    @JSONField(name = "user_id")
    private BigInteger userId;
    @JSONField(name = "friend_id")
    private BigInteger friendId;
    @JSONField(name = "chat_id")
    private BigInteger chatId;
    private String photo;
    @JSONField(name = "group_id")
    private BigInteger groupId;
    private String alias;
    private String description;
    /**
     * 朋友是否被拉黑
     */
    @JSONField(name = "friend_in_black")
    private String friendInBlack;
    /**
     * 朋友是否被举报
     */
    @JSONField(name = "friend_reported")
    private String friendReported;

    public BigInteger getChatId() {
        return chatId;
    }

    public void setChatId(BigInteger chatId) {
        this.chatId = chatId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getFriendId() {
        return friendId;
    }

    public void setFriendId(BigInteger friendId) {
        this.friendId = friendId;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFriendInBlack() {
        return friendInBlack;
    }

    public void setFriendInBlack(String friendInBlack) {
        this.friendInBlack = friendInBlack;
    }

    public String getFriendReported() {
        return friendReported;
    }

    public void setFriendReported(String friendReported) {
        this.friendReported = friendReported;
    }
}
