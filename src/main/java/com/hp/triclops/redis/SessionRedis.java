package com.hp.triclops.redis;

import com.hp.triclops.SimpleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;


/**
 * Created by xiongqing on 2015/8/13.
 */
@Component
@Configuration
public class SessionRedis {

    private StringRedisTemplate stringRedisTemplate;

    private RedisConnectionFactory redisConnectionFactory;

    public SessionRedis(){}

    @Autowired
    public SessionRedis(SimpleConfig simpleConfig){
        this.redisConnectionFactory = simpleConfig.redisConnectionFactory();
        this.stringRedisTemplate = simpleConfig.stringRedisTemplate();
    }

    ValueOperations<String,String> valOpts = null;

    public void SaveSeesion(String seeionId,String sessionValue){

        this.valOpts = this.stringRedisTemplate.opsForValue();
        valOpts.set(seeionId,sessionValue);

    }

    public void UpdateSeesion(String seesionId,String sessionValue){
        this.valOpts = this.stringRedisTemplate.opsForValue();

        if(valOpts.get(seesionId).length() > 0 || !valOpts.get(seesionId).equals("")){
            this.stringRedisTemplate.delete(seesionId);
        }
        valOpts.set(seesionId,sessionValue);

    }

    public void DelSeesion(String seesionId){
        this.valOpts = this.stringRedisTemplate.opsForValue();

        if(valOpts.get(seesionId).length() > 0 || !valOpts.get(seesionId).equals("")){
            this.stringRedisTemplate.delete(seesionId);
        }
    }

}
