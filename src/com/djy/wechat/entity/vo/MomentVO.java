package com.djy.wechat.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.djy.wechat.entity.po.Moment;

/**
 * @description 朋友圈的视图层对象
 */
public class MomentVO extends Moment {

    @JSONField(name = "user_name")
    private String userName;
    private Boolean loved;
    private Boolean viewed;
    @JSONField(name = "user_photo")
    private String userPhoto;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getLoved() {
        return loved;
    }

    public void setLoved(boolean loved) {
        this.loved = loved;
    }

    public boolean getViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

}
