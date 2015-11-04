package com.hp.triclops.utils;

import java.util.Random;

/**
 * Created by wh on 2015/8/13.
 */
public class SaltStringUtil {
    /**
     * 生成SALT随机码
     * @param length 随机码长度
     * @return 返回生成的随机码字符串
     */
    public static String getSaltString(int length){
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer saltStr = new StringBuffer();

        Random random = new Random();
        int range = buffer.length();
        for(int i = 0;i < length;i++){
            saltStr.append(buffer.charAt(random.nextInt(range)));
        }

        return saltStr.toString();

    }
}
