package com.bluemsun.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class RedisService
{
    @Resource
    RedisTemplate redisTemplate;
/*
    public void save(String a, String b) {
        redisTemplate.opsForValue().set(a,b);
    }

    public String query(String a) {
        return (String)redisTemplate.opsForValue().get(a);
    }
    */
}
