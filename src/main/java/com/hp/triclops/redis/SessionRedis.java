package com.hp.triclops.redis;

import com.hp.triclops.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongqing on 2015/8/13.
 */
@Component
public class SessionRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private String[] preStr = {"session:","code:"};
    private String sessionKey = "";

    ValueOperations<String,String> valOpts = null;
    ValueOperations<String,Object> valObjOpts = null;

    /**
     *设置对象存储默认序列化对象
     */
    private void setRedisTemplatePro(){
        this.redisTemplate.setValueSerializer(this.redisTemplate.getDefaultSerializer());
        this.redisTemplate.afterPropertiesSet();

    }

    public String setPreOfKey(int index,String sessionId){
        return this.preStr[index] + sessionId;
    }

    private long defaultExpireSeconds(int hours){
        long expire = 0;
        expire = hours * 3600;
        return expire;
    }

    /**
     * 存储STRING类型数据
     * @param sessionId 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */
    public void saveSessionOfVal(String sessionId,String sessionValue,long ... expireSeconds){

        sessionKey = setPreOfKey(1, sessionId);
        this.valOpts = this.stringRedisTemplate.opsForValue();
        valOpts.set(sessionKey, sessionValue);
        if(expireSeconds.length != 0) {
            this.stringRedisTemplate.expire(sessionKey, expireSeconds[0], TimeUnit.SECONDS);
        }
        else {
            this.stringRedisTemplate.expire(sessionKey, this.defaultExpireSeconds(24), TimeUnit.SECONDS);
        }

    }

    /**
     * 获取键对应的值
     * @param sessionId 键
     * @return 键对应的值
     */
    public String getSessionOfVal(String sessionId){
        sessionKey = setPreOfKey(1,sessionId);
        this.valOpts = this.stringRedisTemplate.opsForValue();
        return valOpts.get(sessionKey);

    }

    /**
     * 更新已存在的键所对应的值
     * @param sessionId 键
     * @param sessionValue 更新的值
     */
    public void updateSessionOfVal(String sessionId,String sessionValue){

        sessionKey = setPreOfKey(1, sessionId);
        long expireSeconds = this.stringRedisTemplate.getExpire(sessionKey);
        this.delSessionOfVal(sessionId);
        this.saveSessionOfVal(sessionId,sessionValue,expireSeconds);

    }

    /**
     * 删除指定键值
     * @param sessionId
     * @return 是否成功，true，成功；false，失败
     */
    public boolean delSessionOfVal(String sessionId){
        sessionKey = setPreOfKey(1, sessionId);
        boolean ret = true;
        this.valOpts = this.stringRedisTemplate.opsForValue();
        if(valOpts.get(sessionKey).length() > 0 || !valOpts.get(sessionKey).equals("")){
            this.stringRedisTemplate.delete(sessionKey);
        }
        else {
            return false;
        }

        return ret;
    }


    /**
     * 存储对象泪类型数据
     * @param sessionId 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */
    public void saveSessionOfList(String sessionId,Object sessionValue,long ... expireSeconds){

        sessionKey = setPreOfKey(0,sessionId);
        this.setRedisTemplatePro();
        this.valObjOpts = this.redisTemplate.opsForValue();
        this.valObjOpts.set(sessionKey, sessionValue);

        if(expireSeconds.length != 0) {
            this.redisTemplate.expire(sessionKey,expireSeconds[0],TimeUnit.SECONDS);
        }
        else {
            this.redisTemplate.expire(sessionKey,this.defaultExpireSeconds(24),TimeUnit.SECONDS);
        }
    }

    /**
     * 获取指定对象
     * @param sessionId 键
     * @return 指定键对应的对象
     */
    public Object getSessionOfList(String sessionId){
        sessionKey = setPreOfKey(0,sessionId);
        this.setRedisTemplatePro();
        this.valObjOpts = this.redisTemplate.opsForValue();
        return this.valObjOpts.get(sessionKey);
    }

    /**
     * 更新已存在的键多对于的值
     * @param sessionId 键
     * @param sessionValue 更新的值
     */
    public void updateSessionOfList(String sessionId,Object sessionValue){
        sessionKey = setPreOfKey(0,sessionId);
        long expireSeconds = this.redisTemplate.getExpire(sessionKey);
        this.delSessionAllOfList(sessionId);
        this.saveSessionOfList(sessionId,sessionValue,expireSeconds);
    }


    /**
     * 删除指定对象
     * @param sessionId 键
     * @return 是否成功，true，成功；false，失败
     */
    public boolean delSessionAllOfList(String sessionId){
        sessionKey = setPreOfKey(0,sessionId);
        boolean ret = true;
        if(this.getSessionOfList(sessionId) != null){
            this.redisTemplate.delete(sessionKey);
        }
        else {
            return false;
        }

        return ret;

    }

}
