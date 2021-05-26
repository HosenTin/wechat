package com.djy.wechat.service.impl;

import com.djy.wechat.dao.ReportDao;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Report;
import com.djy.wechat.service.ReportService;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.constants.Status;
import com.djy.wechat.util.Constants;
import com.djy.wechat.util.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static com.djy.wechat.service.constants.ServiceMessage.DATABASE_ERROR;

/**
 * @description 负责举报相关服务
 */
public class ReportServiceImpl implements ReportService {
    private final ReportDao reportDao = (ReportDao) DaoProxyFactory.getInstance().getProxyInstance(ReportDao.class);

    /**
     * 举报列表
     * @param page
     * @return
     */
    @Override
    public ServiceResult listReport(Integer page) {
        //根据页数信息生成查询参数
        int limit = 10;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        List<Report> chatVOList = new LinkedList<>();
        try {
            List<Report> reportList = reportDao.listReport(limit,offset);
            if (reportList == null || reportList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_CHAT_NOW.message, reportList);
            }
            for (Report report : reportList) {
                if (Constants.ACTIVE_STATUS.equals(report.getStatus())) {
                    report.setStatusText(Constants.ACTIVE_STATUS_TEXT);
                    // 生效时截止日期才有效
                    report.setLockedEndTimeStr(DateUtil.getString(report.getLockedEndTime()));
                } else{
                    report.setStatusText(Constants.INACTIVE_STATUS_TEXT);
                    // 不设置为""页面是undefined
                    report.setLockedEndTimeStr("");
                }
                chatVOList.add(report);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
        }
        return new ServiceResult(Status.SUCCESS, null, chatVOList);
    }

    /**
     * 同意举报
     * @param report
     * @return
     */
    @Override
    public ServiceResult agreeReport(Report report) {
        try {
            report.setStatus(Constants.ACTIVE_STATUS); // 生效
            if (reportDao.update(report) != 1) {
                return new ServiceResult(Status.ERROR, DATABASE_ERROR.message, report);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, report);
        }
        return new ServiceResult(Status.SUCCESS, null, report);
    }
}
