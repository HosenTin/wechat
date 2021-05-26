package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Remark;

import java.math.BigInteger;

/**
 * @description 负责评论服务
 */
public interface RemarkService {
    /**添加一条评论
     * @name addRemark
     * @param remark 评论
     */
    ServiceResult addRemark(Remark remark);
    /**
     * 查询一条朋友圈的评论
     * @param momentId 朋友圈id
     * @param page   页数
     * @name listRemark
     */
    ServiceResult listRemark(BigInteger momentId, int page);
    /**
     * 删除一条评论
     * @param remarkId 评论id
     * @name removeRemark
     */
    ServiceResult removeRemark(BigInteger remarkId);


}
