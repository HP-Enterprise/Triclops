package com.hp.triclops.redis;

import com.hp.triclops.SimpleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;


/**
 * Created by xiongqing on 2015/8/13.
 */
@Component
@Configuration
public class SessionRedis {

    private StringRedisTemplate stringRedisTemplate;

    private RedisTemplate<String,Object> redisTemplate;

    private RedisConnectionFactory redisConnectionFactory;

    public SessionRedis(){}

    @Autowired
    public SessionRedis(SimpleConfig simpleConfig){
        this.redisConnectionFactory = simpleConfig.redisConnectionFactory();
        this.stringRedisTemplate = simpleConfig.stringRedisTemplate();
        this.redisTemplate = simpleConfig.redisTemplate();
    }

    ValueOperations<String,String> valOpts = null;
    ListOperations<String,Object> listOpts = null;

    public void saveSessionOfVal(String sessionId,String sessionValue){

        this.valOpts = this.stringRedisTemplate.opsForValue();
        valOpts.set(sessionId,sessionValue);

    }

    public String getSessionOfVal(String sessionId){
        this.valOpts = this.stringRedisTemplate.opsForValue();
        return valOpts.get(sessionId);

    }

    public void UpdateSessionOfVal(String sessionId,String sessionValue){
        this.valOpts = this.stringRedisTemplate.opsForValue();

        if(valOpts.get(sessionId).length() > 0 || !valOpts.get(sessionId).equals("")){
            this.stringRedisTemplate.delete(sessionId);
        }
        valOpts.set(sessionId,sessionValue);

    }

    public void DelSessionOfVal(String sessionId){
        this.valOpts = this.stringRedisTemplate.opsForValue();

        if(valOpts.get(sessionId).length() > 0 || !valOpts.get(sessionId).equals("")){
            this.stringRedisTemplate.delete(sessionId);
        }
    }

    public void saveSessionOfList(String sessionId,Object sessionValue){
        this.listOpts = this.redisTemplate.opsForList();
        listOpts.leftPush(sessionId,sessionValue);
    }

    public Object getSessionOfList(String sessionId){
        this.listOpts = this.redisTemplate.opsForList();
        return listOpts.index(sessionId,0);
    }

    public void updateSessionOfList(String sessionId,Object sessionValue){
        this.delSessionOfList(sessionId,sessionValue);
        this.saveSessionOfList(sessionId,sessionValue);
    }

    public void delSessionOfList(String sessionId,Object sessionValue){
        this.listOpts = this.redisTemplate.opsForList();
        this.listOpts.remove(sessionId,0,sessionValue);
    }

}
