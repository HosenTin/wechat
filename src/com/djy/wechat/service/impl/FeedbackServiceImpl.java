package com.djy.wechat.service.impl;

import com.djy.wechat.dao.*;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.*;
import com.djy.wechat.service.FeedbackService;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.constants.Status;
import com.djy.wechat.util.Constants;
import com.djy.wechat.util.DateUtil;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.djy.wechat.service.constants.ServiceMessage.*;

/**
 * @description 负责申诉相关服务
 */
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackDao feedbackDao = (FeedbackDao) DaoProxyFactory.getInstance().getProxyInstance(FeedbackDao.class);
    private final ReportDao reportDao = (ReportDao) DaoProxyFactory.getInstance().getProxyInstance(ReportDao.class);

    /**
     * 添加反馈
     */
    @Override
    synchronized public ServiceResult addFeedback(Feedback feedback) {
        if (feedback == null) {
            return new ServiceResult(Status.ERROR, PARAMETER_NOT_ENOUGHT.message, feedback);
        }
        try {
            // 先查询当前用户是否被人举报过
            List<Report> list = reportDao.queryReport(feedback.getUserId(),DateUtil.getString(new Date()));
            if (list == null || list.isEmpty()) {
                return new ServiceResult(Status.ERROR, NOT_REPORTED_YET.message, feedback);
            }
            //添加反馈
            if (feedbackDao.insert(feedback) != 1) {
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, feedback);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, feedback);
        }
        return new ServiceResult(Status.SUCCESS, FEEDBACK_SUCCESS.message, feedback);
    }

    @Override
    public ServiceResult listFeedback(Integer page) {
        //根据页数信息生成查询参数
        int limit = 10;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        List<Feedback> chatVOList = new LinkedList<>();
        try {
            List<Feedback> reportList = feedbackDao.listFeedback(limit,offset);
            if (reportList == null || reportList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_CHAT_NOW.message, reportList);
            }
            for (Feedback feedback : reportList) {
                if (Constants.ACTIVE_STATUS.equals(feedback.getStatus())) {
                    feedback.setStatusText(Constants.ACTIVE_STATUS_TEXT);
                } else{
                    feedback.setStatusText(Constants.INACTIVE_STATUS_TEXT);
                }
                chatVOList.add(feedback);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
        }
        return new ServiceResult(Status.SUCCESS, null, chatVOList);
    }

    /**
     * 同意反馈
     * @param feedbackId
     * @param feedbackUserId
     * @return
     */
    @Override
    public ServiceResult agreeFeedback(String feedbackId, String feedbackUserId) {
        try {
            // 更新反馈表的记录为生效
            Feedback feedback = new Feedback();
            feedback.setId(new BigInteger(feedbackId));
            feedback.setStatus(Constants.ACTIVE_STATUS); // 生效
            if (feedbackDao.update(feedback) != 1) {
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, feedback);
            }
            // 根据反馈人的id,更新该id对应的举报记录为失效
            reportDao.updateReport2Inactive(new BigInteger(feedbackUserId));
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, feedbackId);
        }
        return new ServiceResult(Status.SUCCESS, null, feedbackId);
    }
}
