package com.djy.wechat.entity.po.abs;

import com.alibaba.fastjson.JSONObject;
import com.djy.wechat.dao.annotation.Field;

import java.math.BigInteger;
import java.util.Date;

/**
 * @program wechat
 * @description 所有数据库记录的父类
 */
public abstract class BaseEntity {

    @Field(name = "id")
    private BigInteger id;
    @Field(name = "status")
    private Integer status;
    @Field(name = "gmt_create")
    private Date gmtCreate;
    @Field(name = "gmt_modified")
    private Date gmtModified;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 所有的po层实体类都继承此类，在这里一次性重写toString方法，使用json格式输出
     * @return
     * @name toString
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
