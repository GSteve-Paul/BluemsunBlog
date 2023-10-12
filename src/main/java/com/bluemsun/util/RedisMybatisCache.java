package com.bluemsun.util;

import org.apache.ibatis.cache.Cache;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RedisMybatisCache implements Cache
{

    private static RedisTemplate<Object, Object> template;
    private final String id;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    public RedisMybatisCache(String id) {
        this.id = id;
    }

    public static void setTemplate(RedisTemplate<Object, Object> template) {
        RedisMybatisCache.template = template;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object o, Object o1) {
        long time = Math.abs(new Random().nextLong() % 10 + 5);
        template.opsForValue().set(o, o1, time, TimeUnit.DAYS);
    }

    @Override
    public Object getObject(Object o) {
        return template.opsForValue().get(o);
    }

    @Override
    public Object removeObject(Object o) {
        return template.opsForValue().getAndDelete(o);
    }

    @Override
    public void clear() {
        template.execute(new RedisCallback<Void>()
        {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return null;
            }
        });
    }

    @Override
    public int getSize() {
        return template.execute(new RedisCallback<Integer>()
        {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize().intValue();
            }
        }).intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }
}
