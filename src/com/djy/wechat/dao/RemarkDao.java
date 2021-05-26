package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.entity.po.Remark;

import java.util.List;

public interface RemarkDao extends BaseDao{
    String TABLE = "remark";
    String ALL_FIELD = "user_id,moment_id,content,time," + BASE_FIELD;

    /**
     * 通过用户id逆序查询所有自己发布的朋友圈
     *
     * @param momentId 朋友圈id
     * @param limit  每页查询记录数
     * @param offset 起始记录数
     * @name listRemarkDesc
     */
    @Result(entity = Remark.class, returns = ResultType.LIST)
    @Query("select " + ALL_FIELD + " from " + TABLE + " where moment_id = ?  order by time limit ? offset ?  ")
    List<Remark> listRemarkDesc(Object momentId, int limit, int offset);

    /**
     * 通过评论id查询一个评论
     * @param id 评论id
     * @name getRemarkById
     */
    @Result(entity = Remark.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where id = ? ")
    Remark getRemarkById(Object id);
}
