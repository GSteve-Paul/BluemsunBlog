package com.bluemsun.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.Duration;

@Component
public class JWTUtil
{
    @Resource
    RedisTemplate<Object, Object> redisTemplate2;

    @Value("${token.innerSecret}")
    String innerSecret;

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
        try {
            Claims body = Jwts.parser().setSigningKey(innerSecret).parseClaimsJws(token).getBody();
            Long uuid = getUuid(body);
            String thisType = getType(body);
            if(!uuid.toString().equals(redisTemplate2.opsForValue().get(token))) {
                throw new Exception();
            }
            if(!thisType.equals(type)) {
                throw new Exception();
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public void deleteToken(String token) {
        redisTemplate2.delete(token);
    }

    public Long getUuid(Claims body) {
        return Long.parseLong(body.getSubject());
    }

    public Long getUuid(String token) {
        Claims body = Jwts.parser().setSigningKey(innerSecret).parseClaimsJws(token).getBody();
        return Long.parseLong(body.getSubject());
    }

    public String getType(Claims body) {
        return body.getAudience();
    }

    public String getToken(HttpServletRequest request) {
        return request.getHeader("token");
    }
}
