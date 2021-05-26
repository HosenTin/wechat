package com.djy.wechat.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.djy.wechat.dao.annotation.Table;
import com.djy.wechat.entity.po.abs.BaseEntity;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @description 朋友圈实体类
 */
@Table(name = "moment")
public class Moment extends BaseEntity {
    @JSONField(name = "owner_id")
    private BigInteger ownerId;
    private String content;
    private String photo;
    private Timestamp time;
    private Long love;
    private Long remark;
    private Long share;
    private Long view;
    private Long collect;

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public BigInteger getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(BigInteger ownerId) {
        this.ownerId = ownerId;
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

    public Long getRemark() {
        return remark;
    }

    public void setRemark(Long remark) {
        this.remark = remark;
    }

    public Long getShare() {
        return share;
    }

    public void setShare(Long share) {
        this.share = share;
    }

    public Long getView() {
        return view;
    }

    public void setView(Long view) {
        this.view = view;
    }

    public Long getCollect() {
        return collect;
    }

    public void setCollect(Long collect) {
        this.collect = collect;
    }
}
