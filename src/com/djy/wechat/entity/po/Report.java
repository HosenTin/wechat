package com.djy.wechat.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.djy.wechat.dao.annotation.Table;
import com.djy.wechat.entity.po.abs.BaseEntity;

import java.math.BigInteger;
import java.util.Date;

/**
 *  举报信息表
 */
@Table(name = "report")
public class Report extends BaseEntity {
    /**
     * 举报人id
     */
    @JSONField(name = "user_id")
    private BigInteger userId;
    /**
     * 被举报人id
     */
    @JSONField(name = "friend_id")
    private BigInteger friendId;
    @JSONField(name = "report_cont")
    private String reportCont;
    @JSONField(name = "locked_end_time")
    private Date lockedEndTime;
    /**
     * 页面展示，status的文字
     */
    @JSONField(name = "status_text")
    private String statusText;
    /**
     * 页面展示,锁定截止日期
     */
    @JSONField(name = "locked_end_time_str")
    private String lockedEndTimeStr;

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

    public String getReportCont() {
        return reportCont;
    }

    public void setReportCont(String reportCont) {
        this.reportCont = reportCont;
    }

    public Date getLockedEndTime() {
        return lockedEndTime;
    }

    public void setLockedEndTime(Date lockedEndTime) {
        this.lockedEndTime = lockedEndTime;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getLockedEndTimeStr() {
        return lockedEndTimeStr;
    }

    public void setLockedEndTimeStr(String lockedEndTimeStr) {
        this.lockedEndTimeStr = lockedEndTimeStr;
    }
}
