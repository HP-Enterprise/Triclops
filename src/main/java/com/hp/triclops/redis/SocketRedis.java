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
public class SocketRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /*@Autowired
    private RedisTemplate redisTemplate;*/

    @Autowired
    RedisTemplate<String, Object> objectRedisTemplate;


    private String sessionKey = "";

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



    private long defaultExpireSeconds(int hours){
        long expire = 0;
        expire = hours * 3600;
        return expire;
    }

    /**
     * 存储STRING类型数据
     * @param sessionKey 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */
    public void saveString(String sessionKey,String sessionValue,long ... expireSeconds){

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
    public String getString(String sessionId){
        this.valOpts = this.stringRedisTemplate.opsForValue();
        if(!this.stringRedisTemplate.hasKey(sessionId)){
            return "null";
        }
        return valOpts.get(sessionId);

    }

    /**
     * 更新已存在的键所对应的值
     * @param sessionKey 键
     * @param sessionValue 更新的值
     */
    public void updateString(String sessionKey,String sessionValue){


        long expireSeconds = this.stringRedisTemplate.getExpire(sessionKey);
        this.delString(sessionKey);
        this.saveString(sessionKey,sessionValue,expireSeconds);

    }

    /**
     * 删除指定键值
     * @param sessionKey 键
     * @return 是否成功，true，成功；false，失败
     */
    public boolean delString(String sessionKey){
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
     * 存储对象类型数据
     * @param sessionKey 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */
    public void saveObject(String sessionKey,Object sessionValue,long ... expireSeconds){


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
     * @param sessionKey 键
     * @return 指定键对应的对象
     */
    public Object getObject(String sessionKey){

        this.setRedisTemplatePro();
        this.valObjOpts = this.objectRedisTemplate.opsForValue();
        if(!this.objectRedisTemplate.hasKey(sessionKey)){
            return null;
        }
        return this.valObjOpts.get(sessionKey);
    }

    /**
     * 获取全部session值
     * @param key 匹配字符
     * @return session对象列表
     */
    public List<Object> getObjectList(String key){

        this.setRedisTemplatePro();
        this.valObjOpts = this.objectRedisTemplate.opsForValue();

        Set<String> setKey = this.objectRedisTemplate.keys(key);

        return this.valObjOpts.multiGet(setKey);
    }

    /**
     * 获取符合条件的全部keys
     * @param key 匹配字符
     * @return key列表
     */
    public Set<String> getKeySet(String key){

        this.setRedisTemplatePro();
        this.valObjOpts = this.objectRedisTemplate.opsForValue();
        Set<String> setKey = this.objectRedisTemplate.keys(key);
        return setKey;
    }

    /**
     * 更新已存在的键多对于的值
     * @param sessionKey 键
     * @param sessionValue 更新的值
     */
    public void updateObject(String sessionKey,Object sessionValue){

        long expireSeconds = this.objectRedisTemplate.getExpire(sessionKey);
        this.delObject(sessionKey);
        this.saveObject(sessionKey, sessionValue, expireSeconds);
    }


    /**
     * 删除指定对象
     * @param sessionKey 键
     * @return 是否成功，true，成功；false，失败
     */
    public boolean delObject(String sessionKey){

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
