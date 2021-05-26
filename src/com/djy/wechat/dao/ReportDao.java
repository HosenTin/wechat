package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.dao.annotation.Update;
import com.djy.wechat.entity.po.Report;

import java.math.BigInteger;
import java.util.List;

/**
 * @description 举报表的CRUD
 */
public interface ReportDao extends BaseDao {

    String ALL_FIELD = "user_id,friend_id,report_cont,locked_end_time," + BASE_FIELD;
    String TABLE = "report";

    /**
     * 管理员查询所有举报信息
     *
     * @param limit
     * @param offset
     */
    @Result(entity = Report.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " order by id desc limit ? offset ?")
    List listReport(int limit, int offset);

    @Result(entity = Report.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where user_id =? and friend_id = ? and status = 1")
    Report getReport(Object userId, BigInteger friendId);

    /**
     * 查询userId是否存在被人举报且生效的记录
     * @param userId
     * @return
     */
    @Result(entity = Report.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where friend_id = ? and status = 1 and locked_end_time > ?")
    List<Report> queryReport(BigInteger userId,String currentDate);

    /**
     * 查询所有处于生效状态(status=1)且locked_end_time<=当前日期的任务,
     * 这些都是应该失效的记录
     * @param currentDate
     * @return
     */
    @Result(entity = Report.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where status = 1 and locked_end_time <= ?")
    List<Report> queryShouldInactiveReport(String currentDate);

    /**
     * 更新userid对应的举报记录为失效状态
     * @param userId
     */
    @Update("update " + TABLE + " set status = 0 where friend_id =? and status = 1 ")
    void updateReport2Inactive(BigInteger userId);
}
