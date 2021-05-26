package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Friend;
import com.djy.wechat.entity.po.Moment;

import java.math.BigInteger;

/**
 * @description 负责提供朋友圈相关的服务
 */
public interface MomentService {
    /**
     * 插入一条朋友圈
     * @param moment 朋友圈
     * @name insertMoment
     */
    ServiceResult insertMoment(Moment moment);
    /**
     * 给好友双方初始化朋友圈，互相添加动态
     * @param friend 好友
     * @name initNews
     */
    ServiceResult initNews(Friend friend);
    /**
     * 删除一条朋友圈
     * @param momentId 朋友圈id
     * @name removeMoment
     */
    ServiceResult removeMoment(BigInteger momentId);
    /**
     * 查询一个用户所发的所有朋友圈
     * @param page   页数
     * @param userId 用户id
     * @name listNews
     */
    ServiceResult listMyMoment(BigInteger userId, int page);
    /**
     * 查询一个用户可见的所有朋友圈，包括自己的和朋友的
     * @param userId 用户id
     * @param page   页数
     * @name listNews
     */
    ServiceResult listNews(BigInteger userId, int page);
    /**
     * 更新一个用户对一条朋友圈的点赞状态
     * @param userId   用户id
     * @param momentId 朋友圈id
     * @name love
     */
    ServiceResult love(BigInteger userId, BigInteger momentId);
    /**
     * 查询一个用户朋友圈中的所有图片
     * @param userId 用户id
     * @param page   页数
     * @name loadPhoto
     */
    ServiceResult listPhoto(BigInteger userId, int page);
}
