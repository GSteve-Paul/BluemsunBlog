package com.bluemsun;

import com.bluemsun.service.RedisService;
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
        redisService.save("monkey", "mm");
        System.out.println(redisService.query("monkey"));
    }
}
