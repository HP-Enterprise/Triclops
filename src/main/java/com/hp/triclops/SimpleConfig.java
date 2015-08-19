package com.hp.triclops;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

@Component
@Configuration
public class SimpleConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10000);
        poolConfig.setMinIdle(-1);
        poolConfig.setMaxIdle(1000);
        poolConfig.setMaxWaitMillis(5000);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        JedisConnectionFactory jcf = new JedisConnectionFactory(poolConfig);
        jcf.setHostName("120.55.98.235");
        jcf.setPort(6379);
        jcf.afterPropertiesSet();

        return jcf;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(){

        return new StringRedisTemplate(redisConnectionFactory());
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        return new RedisTemplate<String,Object>(redisConnectionFactory());
    }

}
