package com.djy.wechat.util;

import java.util.UUID;

/**
 * @description 用于提供一个指定长度(11位)的uuid，并去除其中的横线[存在问题：截取可能会破坏uuid的唯一性]
 */
public class UUIDUtils {
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuffer str = new StringBuffer();

        for (int i = 0; i < 11; i++)
        {
            str.append(uuid.charAt(i));
        }

        return str.toString();
    }
}
