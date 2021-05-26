package com.djy.wechat.util;

/**
 * 过滤器排除静态资源url
 */
public class ExcludeResourceUtil {
    //不需要过滤的url
    static String[] urls = {".js",".css",".ico",".jpg",".png"};

    public static boolean shouldExclude(String uri) {
        boolean flag = false;
        for (String str : urls) {
            if (uri.contains(str)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
