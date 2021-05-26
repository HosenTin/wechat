package com.djy.wechat.controller.impl.servlet;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 生成验证码
 */
@WebServlet("/code")
public class CodeServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    /**
     * 图片验证码
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 生成图片的宽高
        int width=100,height=35;
        // 去掉0、O、o、1、l、I 易混淆
        String baseChar="23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
        //1.在内存中创建一张图片
        BufferedImage bi = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        //2.得到图片
        Graphics graphics = bi.getGraphics();
        Graphics2D g=(Graphics2D) graphics;
        //3.设置图片的背影色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        //4.设置图片的边框
        g.setColor(Color.BLUE);
        g.drawRect(1, 1, width - 2, height - 2);
        //5.在图片上画干扰线
        g.setColor(Color.GREEN);
        for (int i = 0; i < 5; i++) {
            int x1 = new Random().nextInt(width);//〈width
            int y1 = new Random().nextInt(height);
            int x2 = new Random().nextInt(width);
            int y2 = new Random().nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        //6.写在图片上随机数
        g.setColor(Color.RED);
        Font font=new Font(g.getFont().getName(),Font.BOLD, 20);
        g.setFont(font);
        StringBuffer sb = new StringBuffer();
        int x = 5;
        String ch ="";
        // 控制字数
        for (int i = 0; i < 4; i++) {
            // 设置字体旋转角度
            int degree = new Random().nextInt() % 30;
            ch = baseChar.charAt(new Random().nextInt(baseChar.length())) + "";
            sb.append(ch);
            // 正向角度
            g.rotate(degree * Math.PI / 180, x, 20);
            g.drawString(ch, x, 20);
            // 反向角度
            g.rotate(-degree * Math.PI / 180, x, 20);
            x += 25;
        }
        //7.将随机数存在session中
        request.getSession().setAttribute("code", sb.toString());
        //8.设置响应头通知浏览器以图片的形式打开
        //等同于response.setHeader("Content-Type", "image/jpeg");
        response.setContentType("image/jpeg");
        //9.设置响应头控制浏览器不要缓存
        response.setDateHeader("expries", 0);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        //10.将图片写给浏览器
        try(ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bi, "jpeg", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            g.dispose();
        }
    }
}


