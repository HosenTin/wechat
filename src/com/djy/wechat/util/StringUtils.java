package com.djy.wechat.util;


import java.util.ArrayList;

/**
 * @description 用于字符串的转换
 */
public class StringUtils {

    /**
     * 返回一个关键词的各种模糊搜索数组
     * @param keyWord
     * @name toLikeSql
     */
    public static String[] toLikeSql(String keyWord) {
        //TODO 添加每个字符的模糊搜索
        ArrayList<String> list = new ArrayList<>();
        String sql = "%" + keyWord + "%";
        list.add(sql);
        String[] strings = new String[list.size()];
        return list.toArray(strings);
    }

    /**
     * 将java变量名转化成mysql的属性名，在每个大写字母的前面加上'_'
     * @param field java属性
     * @return mysql属性名
     * @name field2SqlField
     */
    public static String field2SqlField(String field) {
        byte[] bytes = field.getBytes();
        StringBuilder name = new StringBuilder();
        for (byte aByte : bytes) {
            if (aByte >= 'A' && aByte <= 'Z') {
                name.append('_');
            }
            name.append((char) aByte);
        }
        return name.toString();
    }

    public static String field2SetMethod(String field) {
        StringBuilder method = new StringBuilder("set" + field);
        method.setCharAt(3, (char) (method.charAt(3) - 32));
        return method.toString();
    }

    /**
     * 去除输入中的不合法字符，防止标签注入
     * @param str 需要被过滤的字符
     */
    public static String toLegalText(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        str = toLegalTextIgnoreTag(str);
        String htmlLabel = "<[^>]+>";
        str = str.replaceAll(htmlLabel, "");
        str = str.replace("\"", "");
        str = str.replaceAll("\t|\r|\n", "");
        return str;
    }

    /**
     * 去除输入中的不合法字符，忽略html标签
     *
     * @param str 需要被过滤的字符
     */
    public static String toLegalTextIgnoreTag(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        String styleLabel = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        String scriptLabel = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        str = str.replaceAll(styleLabel, "");
        str = str.replaceAll(scriptLabel, "");
        return str;
    }
}
