package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;

import javax.servlet.http.Part;

/**
 * @description 负责提供文件上传服务
 */
public interface UploadService {

    /**
     * 负责将文件写入文件，并将数据库表对应的记录上的photo属性值修改为文件名
     * @param part      文件
     * @param id        记录id
     * @param tableName 表名
     * @name uploadPhoto
     */
    ServiceResult uploadPhoto(Part part, Object id, String tableName);

    /**
     * 负责将文件写入文件，并将数据库表对应聊天背景属性值修改为文件名
     * @param part      文件
     * @param id        记录id
     * @name uploadBackground
     */
    ServiceResult uploadBackground(Part part, Object id);


    /**
     * 负责将文件写入文件，并返回文件名
     * @param part      文件
     * @name uploadFile
     */
    ServiceResult uploadFile(Part part);


}
