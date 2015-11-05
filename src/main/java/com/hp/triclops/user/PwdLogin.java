package com.hp.triclops.user;

import com.hp.triclops.entity.User;
import com.hp.triclops.redis.SessionRedis;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.utils.DateUtil;
import com.hp.triclops.utils.MD5;
import com.hp.triclops.utils.SaltStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 用户登录类
 * Created by Teemol on 2015/11/3.
 */

@Component
public class PwdLogin {

    @Autowired
    SessionRedis sessionRedis;
    @Autowired
    UserRepository userRepository;


    /**
     * 获取登录信息
     * @param token  登录令牌
     * @return  正常情况下返回登录信息，否则抛出异常code
     */
    public PwdLoginInfo getLoginInfo(String token) throws Exception{

        PwdLoginInfo info = (PwdLoginInfo)sessionRedis.getSessionOfList(token);

        if (info.getId() == 0) {
            return info;
        } else {
            throw new Exception("unauthorized");
        }
    }

    /**
     * 获取所有登录信息
     * @return 登录信息集合
     */
    public List<Object> getAllLoginInfo(){

        return sessionRedis.getSessionOfList();
    }

    /**
     * 通过用户名 + 密码登录
     * @param evidence  登录凭据
     * @return  0：登录成功   -1：登录失败
     */
    public int loginByPwd(PwdEvidence evidence,boolean resCode,HttpServletResponse response){

        if(resCode){
            /**
             * 如果成功,设置cookie(保存在redis服务器中)
             * cookie设置过期策略
             */

            MD5 md5 = new MD5();
            String saltStr = SaltStringUtil.getSaltString(4);
            Date date = new Date();
            String dateStr = DateUtil.formatDateTime(date);  //"yyyy-MM-dd HH:mm:ss"
            String loginWithSaltOfMD5 = md5.getMD5ofStr(evidence.getName() + saltStr + dateStr);
            Cookie cookie = new Cookie("token",loginWithSaltOfMD5);
            int seconds = 60*60*24;  //1天的秒数
            cookie.setMaxAge(seconds);  //cookie默认保存1天
            //设置路径，这个路径即该工程下都可以访问该cookie
            // 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
            cookie.setPath("/");
            response.addCookie(cookie);

            /**
             * 登记到redis服务器
             * 用户id,当前登录的用户ID, 用户名称,用户昵称,登录时间.
             */
            User loginUser = userRepository.findByName(evidence.getName());
            PwdLoginInfo loginInfo = new PwdLoginInfo();   //登录信息

            loginInfo.setId(loginUser.getId());
            loginInfo.setName(loginUser.getName());
            loginInfo.setNick(loginUser.getNick());
            loginInfo.setLoginDate(dateStr);
            sessionRedis.saveSessionOfList(loginWithSaltOfMD5, loginInfo, seconds);

            return 0;  //登陆成功
        }else{
            return -1;  //登录失败
        }

    }

    /**
     * 用户注销
     * @param cookies cookies集合
     * @return  0：注销成功   -1：注销失败
     */
    public int logout(Cookie[] cookies,HttpServletResponse response){

        /**
         * 从redis的session管理器中,注销对应的对象
         * 注销当前用户的cookie
         */
        if(cookies !=null){
            for(Cookie c :cookies ){
                if(c.getName().equals("token")||c.getName().equals("token_resetPassword")){
                    /**
                     * 根据c.getValue ，注销对应的对象
                     */
                    c.setMaxAge(0);
                    c.setPath("/");
                    response.addCookie(c);
                    boolean delStatus = sessionRedis.delSessionAllOfList(c.getValue());
                    if(delStatus){
                        return 0;
                    }

                }
            }
        }
        return -1;
    }

}
