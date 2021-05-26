package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.entity.po.News;

import java.util.List;

/**
 * @description 负责动态表的CRUD
 */
public interface NewsDao extends BaseDao {
    String TABLE = "news";
    String ALL_FIELD = "user_id,moment_id,loved,viewed," + BASE_FIELD;

    /**
     * 通过用户id查询所有自己可见的所有朋友圈动态
     * @param userId 用户id
     * @param limit  每页查询记录数
     * @param offset 起始记录数
     * @name listNewsByUserId
     */
    @Result(entity = News.class, returns = ResultType.LIST)
    @Query("select " + ALL_FIELD + " from " + TABLE + " where user_id = ?  order by gmt_create desc limit ? offset ?  ")
    List<News> listNewsByUserId(Object userId, int limit, int offset);

    @Result(entity = News.class, returns = ResultType.LIST)
    @Query("select " + ALL_FIELD + " from " + TABLE + " order by gmt_create desc limit ? offset ?  ")
    List<News> listAllNews(int limit, int offset);

    /**
     * 通过朋友圈id和用户id查询一个朋友圈动态
     * @param momentId 朋友圈id
     * @param userId 用户id
     * @name getNewsByMomentId
     */
    @Result(entity = News.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where moment_id = ? and user_id = ? ")
    News getNewsByMomentIdAndUserId(Object momentId,Object userId);
}
