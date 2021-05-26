package com.djy.wechat.util;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @email：548395517@qq.com
 * @description：发送邮件的工具类
 */
public class MailUtils extends Thread{
    //发件邮箱
    private String from = "548395517@qq.com";
    //邮箱的用户名
    private String username= "548395517@qq.com";
    //邮箱的授权码
    private String password = "hydhbckcpxuibbcb";
    //邮箱服务器地址
    private String host = "smtp.qq.com";
    //邮箱验证码
    private String mailCode;
    //邮箱发送地址
    private String toMail;

    public MailUtils() {
        mailCode = CodeUtils.generateText();
    }

    public void send(String toMail) {
        this.toMail = toMail;
        // 启动线程
        this.start();
    }

    /**
     * 启动线程自动调用run方法
     */
    @Override
    public void run() {
        try {
            //判断填写的邮箱是否为空
            if (toMail == null || "".equals(toMail.trim())) {
                return;
            }
            Properties properties = buildProps();
            Session session = Session.getInstance(properties);
            Transport transport = session.getTransport();
            transport.connect(host,username,password);
            //发送的邮箱地址
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(toMail)});
            //邮件标题
            mimeMessage.setSubject("微信网页版邮箱验证码");
            //mimeMessage.setContent("","text/html;charset=utf-8");
            //邮件内容
            mimeMessage.setText("欢迎注册微信网页版，您的注册邮箱验证码为："+mailCode+"，请您记住注册时的邮箱和密码(用于登录)并在有效期内完成注册");
            //通过transport对象传输邮件
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("发送邮件异常");
        }
    }

    private Properties buildProps() throws GeneralSecurityException {
        Properties properties = new Properties();
        properties.setProperty("mail.host",host);
        properties.setProperty("mail.transport.protocol","smtp");
        properties.setProperty("mail.smtp.auth","true");
        MailSSLSocketFactory sf  = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        return properties;
    }
    //get
    public String getMailCode() {
        return mailCode;
    }
}
