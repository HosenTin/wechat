package com.djy.wechat.task;

import com.djy.wechat.dao.ReportDao;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.po.Report;
import com.djy.wechat.util.Constants;
import com.djy.wechat.util.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * 查询所有处于生效状态(status=1)且locked_end_time<=当前日期的任务
 */
public class ReportTask extends TimerTask {
    private final ReportDao reportDao = (ReportDao) DaoProxyFactory.getInstance().getProxyInstance(ReportDao.class);
    @Override
    public void run() {
        List<Report> reportList = reportDao.queryShouldInactiveReport(DateUtil.getString(new Date()));
        if (reportList == null || reportList.isEmpty()) {
            return;
        }
        for (Report report : reportList) {
            report.setStatus(Constants.INACTIVE_STATUS);
            reportDao.update(report);
        }
    }
}
