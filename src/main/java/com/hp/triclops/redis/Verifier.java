package com.hp.triclops.redis;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 校验器
 * 产生临时性的随机校验码
 * 然后进行一致性校验
 */
public class Verifier {
    /**
     * 产生一个6位的纯数字的随机校验码
     * @param 校验码目标
     * @param 过期时间(秒)
     * @return 成功返回纯数字的6位校验码
     */
    public String generateCode(String target, int expiredSeconds){
        throw new NotImplementedException();
    }

    /**
     * 校验
     * @param target 校验目标
     * @param code 校验码
     * @return 成功返回TRUE, 失败返回FALSE
     */
    public boolean verifyCode(String target, String code){
        throw new NotImplementedException();
    }
}
