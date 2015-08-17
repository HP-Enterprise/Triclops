package com.hp.triclops;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
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
        poolConfig.setMinIdle(1000);
        poolConfig.setMaxIdle(-1);
        poolConfig.setMaxWaitMillis(500);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        return new JedisConnectionFactory(poolConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(){
        return new StringRedisTemplate(redisConnectionFactory());
    }
}
