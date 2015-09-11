package com.hp.triclops.utils;

/**
 * Created by luj on 2015/9/11.
 */
public class EscapeStringUtil {

    /**
     * 转义字符处理 传入:abc%def_ghi 返回:abc\%def\_ghi
     * @param str 带特殊字符的字符串
     * @return 处理转义字符后的字符串
     */
    public static String toEscape(String str){
        if(str==null)
            return null;
        return str.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").replace("'", "\\'");
    }
}

