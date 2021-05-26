package com.djy.wechat.service;

import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Report;

/**
 * 负责申诉相关业务
 */
public interface ReportService {
    /**
     * 被举报用户的列表
     * @param page
     */
    ServiceResult listReport(Integer page);

    /**
     * 同意举报
     * @param report
     * @return
     */
    ServiceResult agreeReport(Report report);
}
