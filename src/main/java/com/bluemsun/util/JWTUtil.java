package com.bluemsun.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JWTUtil
{
    @Resource
    RedisTemplate<Object, Object> redisTemplate2;

    @Value("${token.innerSecret}")
    String innerSecret;

    @Value("${token.outSecret}")
    String outSecret;

    public String createToken(String username) {
        String token = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 604800000L))
                .signWith(SignatureAlgorithm.HS512, innerSecret)
                .compact();
        redisTemplate2.opsForValue().setIfAbsent(token, username, 7, TimeUnit.DAYS);
        return token;
    }

    public boolean checkToken(String token) {
        Claims body = Jwts.parser().setSigningKey(innerSecret).parseClaimsJws(token).getBody();
        String username = body.getSubject();
        return redisTemplate2.opsForValue().get(token) == username;
    }

    public String updateToken(String token) {
        Claims body = Jwts.parser().setSigningKey(innerSecret).parseClaimsJws(token).getBody();
        String username = body.getSubject();
        String newToken = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(SignatureAlgorithm.ES256, innerSecret)
                .compact();
        redisTemplate2.opsForValue().setIfPresent(newToken, username, 7, TimeUnit.DAYS);
        return newToken;
    }

    public void deleteToken(String token) {
        redisTemplate2.delete(token);
    }
}
