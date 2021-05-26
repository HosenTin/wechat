package com.djy.wechat.provider;

import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.proxy.ServiceProxyFactory;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Report;
import com.djy.wechat.provider.annotation.Action;
import com.djy.wechat.provider.annotation.ActionProvider;
import com.djy.wechat.service.ReportService;
import com.djy.wechat.service.impl.ReportServiceImpl;
import com.djy.wechat.util.BeanUtils;
import com.djy.wechat.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.djy.wechat.service.constants.ServiceMessage.*;
import static com.djy.wechat.service.constants.Status.ERROR;
import static com.djy.wechat.util.ControllerUtils.returnJsonObject;

/**
 * @description 用于处理反馈相关业务流程
 */
@ActionProvider(path = "/report")
public class ReportProvider extends Provider {

    private final ReportService reportService = (ReportService) new ServiceProxyFactory().getProxyInstance(new ReportServiceImpl());

    /**
     * 提供申诉的业务流程
     */
    @Action(method = RequestMethod.LIST_DO)
    public void addFeedback(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String page = req.getParameter("page");
        ServiceResult result;
        //插入申诉内容
        result = reportService.listReport(new Integer(page));
        returnJsonObject(resp, result);
    }
    /**
     * 同意举报
     */
    @Action(method = RequestMethod.AGREEREPORT_DO)
    public void agreeReport(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String reportId = req.getParameter("id");
        ServiceResult result = new ServiceResult();
        // id不能为空
        if (reportId == null || "".equals(reportId.trim())) {
            result.setMessage(PARAMETER_NOT_ENOUGHT.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        // 解封时间不能为空，且符合格式
        String lockedEndTime = req.getParameter("lockedEndTime");
        Date lockedEndTimeDate = null;
        try {
            lockedEndTimeDate = DateUtil.getDate(lockedEndTime);
        }catch (Exception e) {
            e.printStackTrace();
            result.setMessage(PATTERN_NOT_RIGHT.message);
            result.setStatus(ERROR);
            returnJsonObject(resp, result);
            return;
        }
        Report report = (Report) BeanUtils.toObject(req.getParameterMap(), Report.class);
        report.setLockedEndTime(lockedEndTimeDate);
        result = reportService.agreeReport(report);
        returnJsonObject(resp, result);
    }
}
