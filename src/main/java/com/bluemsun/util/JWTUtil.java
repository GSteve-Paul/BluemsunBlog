package com.bluemsun.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.Duration;
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

    public String createToken(Long uuid, String type) {
        String token = Jwts.builder()
                .setSubject(uuid.toString())
                .setAudience(type)
                .setExpiration(new Date(System.currentTimeMillis() + 604800000L))
                .signWith(SignatureAlgorithm.HS512, innerSecret)
                .compact();
        redisTemplate2.opsForValue().setIfAbsent(token, uuid.toString(), Duration.ofDays(7));
        return token;
    }

    public boolean checkToken(String token, String type) {
        Long uuid = getUuid(token);
        String tokenType = getType(token);
        return uuid.toString().equals(redisTemplate2.opsForValue().get(token)) && type.equals(tokenType);
    }

    public void deleteToken(String token) {
        redisTemplate2.delete(token);
    }

    public Long getUuid(String token) {
        Claims body = Jwts.parser().setSigningKey(innerSecret).parseClaimsJws(token).getBody();
        return Long.parseLong(body.getSubject());
    }

    public String getType(String token) {
        Claims body = Jwts.parser().setSigningKey(innerSecret).parseClaimsJws(token).getBody();
        return body.getAudience();
    }
}
