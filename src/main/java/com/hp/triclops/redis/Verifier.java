package com.hp.triclops.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Random;

/**
 * 校验器
 * 产生临时性的随机校验码
 * 然后进行一致性校验
 */
@Component
public class Verifier {
    @Autowired
    SessionRedis sessionRedis;
    /**
     * 产生一个6位的纯数字的随机校验码
     * @param target 校验码目标
     * @param expiredSeconds 过期时间(秒)
     * @return 成功返回纯数字的6位校验码
     */
    public String generateCode(String target, int expiredSeconds){
        StringBuffer buffer = new StringBuffer("0123456789");
        StringBuffer saltStr = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for(int i = 0;i < 6;i++){
            saltStr.append(buffer.charAt(random.nextInt(range)));
        }
        sessionRedis.saveSessionOfVal(target,saltStr.toString(),expiredSeconds);
        return saltStr.toString();
    }

    /**
     * 校验
     * @param target 校验目标
     * @param code 校验码
     * @return 成功返回0，失败返回1，验证码过期返回2
     */
    public int verifyCode(String target, String code){
        int result=2;
        String trueCode=sessionRedis.getSessionOfVal(target);
        if (trueCode!=null)
        {
            if(trueCode .equals(code)){
                result=0;
            }
            else {
                result=1;
            }
        }
        return result;
    }
}
