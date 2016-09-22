package com.hp.triclops.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class SocketRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> objectRedisTemplate;

    ValueOperations<String,String> valOpts = null;
    HashOperations<String,String,String> hashOpts=null;

    SetOperations<String,String> setOpts = null;

    /**
     *设置对象存储默认序列化对象
     */
    private void setRedisTemplatePro(){
        this.objectRedisTemplate.setKeySerializer(this.objectRedisTemplate.getStringSerializer());
        this.objectRedisTemplate.setValueSerializer(this.objectRedisTemplate.getDefaultSerializer());
        this.objectRedisTemplate.afterPropertiesSet();
    }


    /**
     * 存储STRING类型数据 HASH
     * @param hashName 键
     * @param key 值
     * @param value 该键值的过期时间，单位秒
     */

    public void saveHashString(String hashName,String key,String value,long expireSeconds){
        this.hashOpts = this.stringRedisTemplate.opsForHash();
        hashOpts.put(hashName, key, value);
        if(expireSeconds>0) {//不设置即为-1永不过期
            this.stringRedisTemplate.expire(hashName, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 删除 HASH key
     * @param hashName 键
     * @param key 值
     */
    public void deleteHashString(String hashName,String key){
        this.hashOpts = this.stringRedisTemplate.opsForHash();
        hashOpts.delete(hashName, key);
    }

    /**
     * 读取值 HASH key
     * @param hashName 键
     * @param key 值
     */
    public String getHashString(String hashName,String key){
        this.hashOpts = this.stringRedisTemplate.opsForHash();
        return hashOpts.get(hashName, key);
    }

    /**
     * 是否存在 HASH key
     * @param hashName 键
     * @param key 值
     */
    public boolean existHashString(String hashName,String key){
        this.hashOpts = this.stringRedisTemplate.opsForHash();
        boolean re=hashOpts.hasKey(hashName,key);
        return re;
    }


    /**
     * 列出HASH的所有key
     * @param hashName 键
     */
    public Set<String> listHashKeys(String hashName){
        this.hashOpts = this.stringRedisTemplate.opsForHash();
        return hashOpts.keys(hashName);
    }


    /**
     * 存储STRING类型数据 SET
     * @param sessionKey 键
     * @param sessionValue 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */

    public void saveSetString(String sessionKey,String sessionValue,long expireSeconds){
        this.setOpts = this.stringRedisTemplate.opsForSet();
        setOpts.add(sessionKey, sessionValue);
        if(expireSeconds>0) {//不设置即为-1永不过期
            this.stringRedisTemplate.expire(sessionKey, expireSeconds, TimeUnit.SECONDS);
        }
    }



    /**
     * 获取键对应的值，取出后值会删除
     * @param sessionId 键
     * @return 键对应的值
     */
    public String popSetOneString(String sessionId){
        this.setOpts = this.stringRedisTemplate.opsForSet();
        if(!this.stringRedisTemplate.hasKey(sessionId)){
            return "null";
        }
        return  setOpts.pop(sessionId);

    }



    /**
     * 获取键对应的值
     * @param key 键
     * @return 键对应的值
     */
    public String getValueString(String key){
        this.valOpts = this.stringRedisTemplate.opsForValue();
        if(!this.stringRedisTemplate.hasKey(key)){
            return "null";
        }
        String value=valOpts.get(key);
        return  value;
    }

    /**
     * 存储Value数据
     * @param key 键
     * @param value 值
     * @param expireSeconds 该键值的过期时间，单位秒
     */

    public void saveValueString(String key,String value,long expireSeconds){
        this.valOpts = this.stringRedisTemplate.opsForValue();
        valOpts.set(key, value);
        if(expireSeconds>0) {//不设置即为-1永不过期
            this.stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }


    /**
     * 删除指定键值
     * @param key 键
     * @return 是否成功，true，成功；false，失败
     */
    public boolean delValueString(String key){
        boolean ret = true;
        if(this.stringRedisTemplate.hasKey(key)){
            this.stringRedisTemplate.delete(key);
        }
        else {
            return false;
        }
        return ret;
    }



    /**
     * 指定key的全部value
     * @param key 匹配字符
     * @return key列表
     */
    public Set<String> getSmembers(String key){
        this.setRedisTemplatePro();
        this.setOpts = this.stringRedisTemplate.opsForSet();
        Set<String> setKey = setOpts.members(key);
        return setKey;
    }




    /**
     * 获取符合条件的全部keys
     * @param key 匹配字符
     * @return key列表
     */
    public Set<String> getKeysSet(String key){
        this.setRedisTemplatePro();
        Set<String> setKey = this.objectRedisTemplate.keys(key);
        return setKey;
    }



}
