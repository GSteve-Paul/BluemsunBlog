package com.bluemsun.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeRedisService
{
    @Resource
    StringRedisTemplate redisTemplate5;

    public String getVerificationCode(Long uuid) {
        //get the code
        Integer code = Math.abs(new Random().nextInt() % 10000);
        String strCode = code.toString();
        int len = strCode.length();
        StringBuffer pre0 = new StringBuffer();
        for(int i=1;i<=4 - len;i++) {
            pre0.append("0");
        }
        strCode = pre0.toString() + strCode;
        //save in redis
        redisTemplate5.opsForValue().set(strCode,uuid.toString(),5, TimeUnit.MINUTES);
        return strCode;
    }

    public Boolean checkVerificationCode(String code,Long uuid) {
        String val = redisTemplate5.opsForValue().get(code);
        if(val == null) {
            return false;
        }
        if(!val.equals(uuid.toString())) {
            return false;
        }
        redisTemplate5.delete(code);
        return true;
    }
}
