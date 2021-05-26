package com.djy.wechat.entity.builder;

import com.djy.wechat.entity.vo.MomentVO;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @description 负责朋友圈视图层对象的建造
 */
public class MomentVOBuilder {
    private MomentVO momentVO;

    public MomentVOBuilder() {
        this.momentVO = new MomentVO();
    }

    public MomentVO build() {
        return this.momentVO;
    }


    public MomentVOBuilder setTime(Timestamp time){
        this.momentVO.setTime(time);
        return this;
    }

    public MomentVOBuilder setUserPhoto(String userPhoto){
        this.momentVO.setUserPhoto(userPhoto);
        return this;
    }


    public MomentVOBuilder setPhoto(String photo){
        this.momentVO.setPhoto(photo);
        return this;
    }

    public MomentVOBuilder setUserName(String userName){
        this.momentVO.setUserName(userName);
        return this;
    }

    public MomentVOBuilder setId(BigInteger id) {
        this.momentVO.setId(id);
        return this;
    }

    public MomentVOBuilder setUserId(BigInteger userId) {
        this.momentVO.setOwnerId(userId);
        return this;
    }

    public MomentVOBuilder setContent(String content) {
        this.momentVO.setContent(content);
        return this;
    }

    public MomentVOBuilder setShare(Long share) {
        this.momentVO.setShare(share);
        return this;
    }

    public MomentVOBuilder setLove(Long love) {
        this.momentVO.setLove(love);
        return this;
    }
    public MomentVOBuilder setRemark(Long remark) {
        this.momentVO.setRemark(remark);
        return this;
    }
    public MomentVOBuilder setView(Long view) {
        this.momentVO.setView(view);
        return this;
    }
    public MomentVOBuilder setCollect(Long collect) {
        this.momentVO.setCollect(collect);
        return this;
    }
    public MomentVOBuilder setLoved(Boolean loved) {
        this.momentVO.setLoved(loved);
        return this;
    }
    public MomentVOBuilder setViewed(Boolean viewed) {
        this.momentVO.setViewed(viewed);
        return this;

    }
}
