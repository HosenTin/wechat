package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.entity.po.Feedback;

import java.util.List;

/**
 * @description 申诉表的CRUD
 */
public interface FeedbackDao extends BaseDao {

    String ALL_FIELD = "user_id,feedback_cont," + BASE_FIELD;
    String TABLE = "feedback";

    @Result(entity = Feedback.class, returns = ResultType.LIST)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " order by id desc limit ? offset ?")
    List<Feedback> listFeedback(int limit, int offset);
}
