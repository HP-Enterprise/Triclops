package com.hp.triclops.utils;

/**
 * Created by liz on 2015/9/30.
 */
public class FilterString {
    /**
     * 字符串过滤，防sql注入
     *
     * @param str 要过滤的字符串
     * @return
     */
    public static String TransactSQLInjection(String str)
    {
        return str.replaceAll(".*([';]+|(--)+).*", " ");
    }
}
