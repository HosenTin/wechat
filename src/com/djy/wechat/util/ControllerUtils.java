package com.djy.wechat.util;

import com.alibaba.fastjson.JSON;
import com.djy.wechat.controller.constant.RequestMethod;
import com.djy.wechat.entity.dto.ServiceResult;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program wechat
 * @description 控制层工具类
 */
public class ControllerUtils {
    /**
     * 返回请求中method对应的RequestMethod枚举项
     * 工具类中一般都为static方法
     * @param name 请求中的method参数值,比如register.do
     * @see RequestMethod
     */
    public static RequestMethod valueOf(String name) {
        try {
            //register.do—>REGISTER_DO
            name = name.toUpperCase().replaceAll("\\.", "_");
            return RequestMethod.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            //此处异常表明请求参数没有匹配到任何服务,为无效请求
            e.printStackTrace();
            return RequestMethod.INVALID_REQUEST;
        }
    }

    /**
     * 用户将服务结果转换成json数据并返回客户端
     *
     * @param resp   响应
     * @param result 服务结果
     * @return
     * @name returnJsonObject
     */
    public static void returnJsonObject(HttpServletResponse resp, ServiceResult result) throws IOException {
        //将结果转成json字符串
        JSON json = (JSON) JSON.toJSON(result);
        //写入客户端
        resp.getWriter().write(json.toJSONString());
    }
}

