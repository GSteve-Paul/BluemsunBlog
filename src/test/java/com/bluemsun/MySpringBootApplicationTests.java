package com.bluemsun;

import com.bluemsun.entity.User;
import com.bluemsun.service.RedisService;
import com.bluemsun.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MySpringBootApplicationTests
{
    @Resource
    RedisService redisService;

    @Test
    void contextLoads() {
        redisService.save("monkey","mm");
        System.out.println(redisService.query("monkey"));
    }
}
