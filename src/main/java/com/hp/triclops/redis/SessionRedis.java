package com.hp.triclops.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongqing on 2015/8/13.
 */
@Component
public class SessionRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /*@Autowired
    private RedisTemplate redisTemplate;*/

    @Autowired
    RedisTemplate<String, Object> objectRedisTemplate;

    private String[] preStr = {"session:","code:"};
    private String sessionKey = "";
    private static final String ONLINE = "online:";

    ValueOperations<String,String> valOpts = null;
    ValueOperations<String,Object> valObjOpts = null;

    /**
     *设置对象存储默认序列化对象
     */
    private void setRedisTemplatePro(){
        this.objectRedisTemplate.setKeySerializer(this.objectRedisTemplate.getStringSerializer());
        this.objectRedisTemplate.setValueSerializer(this.objectRedisTemplate.getDefaultSerializer());
        this.objectRedisTemplate.afterPropertiesSet();
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

        if(!this.stringRedisTemplate.hasKey(sessionKey)){
            valOpts.set(sessionKey, sessionValue);
            if(expireSeconds.length != 0) {
                this.stringRedisTemplate.expire(sessionKey, expireSeconds[0], TimeUnit.SECONDS);
            }
            else {
                this.stringRedisTemplate.expire(sessionKey, this.defaultExpireSeconds(24), TimeUnit.SECONDS);
            }
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
        if(!this.stringRedisTemplate.hasKey(sessionKey)){
            return null;
        }
        return valOpts.get(sessionKey);
    }

    /**
     * 存储STRING类型数据
     * @param sessionId 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */
    public void saveOnlineSessionOfVal(String sessionId,String sessionValue,long ... expireSeconds){

        sessionKey = ONLINE + sessionId;
        this.valOpts = this.stringRedisTemplate.opsForValue();

        if(!this.stringRedisTemplate.hasKey(sessionKey)){
            valOpts.set(sessionKey, sessionValue);
            if(expireSeconds.length != 0) {
                this.stringRedisTemplate.expire(sessionKey, expireSeconds[0], TimeUnit.SECONDS);
            }
            else {
                this.stringRedisTemplate.expire(sessionKey, this.defaultExpireSeconds(24), TimeUnit.SECONDS);
            }
        }

    }

    /**
     * 获取键对应的值
     * @param sessionId 键
     * @return 键对应的值
     */
    public String getOnlineSessionOfVal(String sessionId){
        sessionKey = ONLINE + sessionId;
        this.valOpts = this.stringRedisTemplate.opsForValue();
        if(!this.stringRedisTemplate.hasKey(sessionKey)){
            return null;
        }
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
     * @param sessionId 键
     * @return 是否成功，true，成功；false，失败
     */
    public boolean delSessionOfVal(String sessionId){
        sessionKey = setPreOfKey(1, sessionId);
        boolean ret = true;
        if(this.stringRedisTemplate.hasKey(sessionKey)){
            this.stringRedisTemplate.delete(sessionKey);
        }
        else {
            return false;
        }

        return ret;
    }

    /**
     * 键值加1
     * @param sessionId 键
     */
    public void increment(String sessionId,int seconds) {

        String val = getSessionOfVal(sessionId);
        if(val==null)
        {
            saveSessionOfVal(sessionId,"1",seconds);
        }
        else
        {
            int valTemp = Integer.valueOf(val) + 1;
            val = String.valueOf(valTemp);
            updateSessionOfVal(sessionId,val);
        }
    }

    /**
     * 存储对象类型数据
     * @param sessionId 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */
    public void saveSessionOfList(String sessionId,Object sessionValue,long ... expireSeconds){

        sessionKey = setPreOfKey(0,sessionId);
        this.setRedisTemplatePro();
        this.valObjOpts = this.objectRedisTemplate.opsForValue();

        if(!this.objectRedisTemplate.hasKey(sessionKey)){
            this.valObjOpts.set(sessionKey, sessionValue);

            if(expireSeconds.length != 0) {
                this.objectRedisTemplate.expire(sessionKey,expireSeconds[0],TimeUnit.SECONDS);
            }
            else {
                this.objectRedisTemplate.expire(sessionKey,this.defaultExpireSeconds(24),TimeUnit.SECONDS);
            }
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
        this.valObjOpts = this.objectRedisTemplate.opsForValue();
        if(!this.objectRedisTemplate.hasKey(sessionKey)){
            return null;
        }
        return this.valObjOpts.get(sessionKey);
    }

    /**
     * 获取全部session值
     * @return session对象列表
     */
    public List<Object> getSessionOfList(){

        this.setRedisTemplatePro();
        this.valObjOpts = this.objectRedisTemplate.opsForValue();

        Set<String> setKey = this.objectRedisTemplate.keys("session*");

        return this.valObjOpts.multiGet(setKey);
    }

    /**
     * 更新已存在的键多对于的值
     * @param sessionId 键
     * @param sessionValue 更新的值
     */
    public void updateSessionOfList(String sessionId,Object sessionValue){
        sessionKey = setPreOfKey(0,sessionId);
        long expireSeconds = this.objectRedisTemplate.getExpire(sessionKey);
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
        if(this.objectRedisTemplate.hasKey(sessionKey)){
            this.objectRedisTemplate.delete(sessionKey);
        }
        else {
            return false;
        }

        return ret;

    }

}
