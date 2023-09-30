package com.bluemsun.config;

import com.bluemsun.util.RedisMybatisCache;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class CacheConfig {
    @Resource
    RedisTemplate<Object, Object> template;

    @PostConstruct
    public void init(){
        //把RedisTemplate给到RedisMybatisCache
        RedisMybatisCache.setTemplate(template);
    }
}
