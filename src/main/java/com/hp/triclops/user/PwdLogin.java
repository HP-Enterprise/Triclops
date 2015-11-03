package com.hp.triclops.user;

import javax.servlet.http.Cookie;
import java.util.List;

/**
 * 用户登录类
 * Created by Teemol on 2015/11/3.
 */
public class PwdLogin {

    /**
     * 获取登录信息
     * @param token  登录令牌
     * @return  正常情况下返回登录信息，否则抛出异常code
     */
    public PwdLoginInfo getLoginInfo(String token){
        return null;
    }

    /**
     * 获取所有登录信息
     * @return 登录信息集合
     */
    public List<Object> getAllLoginInfo(){

        return null;
    }

    /**
     * 通过用户名 + 密码登录
     * @param evidence  登录凭据
     * @return  0：登录成功   -1：登录失败
     */
    public int loginByPwd(PwdEvidence evidence){

        return 0;
    }

    /**
     * 用户注销
     * @param cookies cookies集合
     * @return  0：注销成功   -1：注销失败
     */
    public int logout(Cookie[] cookies){

        return 0;
    }

}
