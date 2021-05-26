package com.djy.wechat.controller.impl.listener;

import com.djy.wechat.provider.*;
import com.djy.wechat.task.ReportTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program wechat
 * @description 负责监听servlet的初始化和销毁事件
 */

@WebListener
public class ServletContextListener implements javax.servlet.ServletContextListener {
    //HashMap
    private static final ConcurrentHashMap<String, Provider> providerMap = new ConcurrentHashMap<>();

    /**
     * tomcat启动成功时会调用(初始化)
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /**
         * 用来装载provider的容器[服务器启动时初始化好]
         */
        providerMap.put("userProvider", new UserProvider());
        providerMap.put("chatProvider", new ChatProvider());
        providerMap.put("uploadProvider", new UploadProvider());
        providerMap.put("friendProvider", new FriendProvider());
        providerMap.put("momentProvider",new MomentProvider());
        providerMap.put("messageProvider",new MessageProvider());
        providerMap.put("remarkProvider",new RemarkProvider());
        providerMap.put("feedbackProvider",new FeedbackProvider());
        providerMap.put("reportProvider",new ReportProvider());
        /**
         * 将providerMap[多种provider集合]放入ServletContext
         * key(xxxProvider):userProvider | value(new XxxProvider()):UserProvider()对象
         * 请求来时可通过key来获取相应的对象
         */
        ServletContext sc = sce.getServletContext();
        sc.setAttribute("providerMap", providerMap);
        // 启动定时器
        Timer timer = new Timer();
        // 通过Timer定时定频率调用ReportTask
        timer.schedule(new ReportTask(), 3000L, 10000L);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
