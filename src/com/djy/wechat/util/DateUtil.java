package com.djy.wechat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * time
 */
public class DateUtil {
    private static final String pattern="yyyy-MM-dd HH:mm:ss";

    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return sdf.parse(date);
    }

    public static String getString(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
