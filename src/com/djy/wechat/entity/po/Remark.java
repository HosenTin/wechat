package com.djy.wechat.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.djy.wechat.dao.annotation.Table;
import com.djy.wechat.entity.po.abs.BaseEntity;

import java.math.BigInteger;
import java.sql.Timestamp;

@Table(name = "remark")
public class Remark extends BaseEntity {

  @JSONField(name = "user_id")
  private BigInteger userId;
  @JSONField(name = "moment_id")
  private BigInteger momentId;
  private String content;
  private Timestamp time ;
  private Long love;
  private Long collect;
  private Long reply;

  public Timestamp getTime() {
    return time;
  }

  public void setTime(Timestamp time) {
    this.time = time;
  }

  public BigInteger getUserId() {
    return userId;
  }

  public void setUserId(BigInteger userId) {
    this.userId = userId;
  }


  public BigInteger getMomentId() {
    return momentId;
  }

  public void setMomentId(BigInteger momentId) {
    this.momentId = momentId;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public Long getLove() {
    return love;
  }

  public void setLove(Long love) {
    this.love = love;
  }

  public Long getCollect() {
    return collect;
  }

  public void setCollect(Long collect) {
    this.collect = collect;
  }

  public Long getReply() {
    return reply;
  }

  public void setReply(Long reply) {
    this.reply = reply;
  }
}
