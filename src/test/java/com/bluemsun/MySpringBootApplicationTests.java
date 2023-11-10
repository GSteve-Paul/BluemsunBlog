package com.bluemsun;

import com.bluemsun.service.CommentService;
import com.bluemsun.service.HotBlogRedisService;
import com.bluemsun.service.LikeBlogRedisService;
import com.bluemsun.service.MailService;
import com.bluemsun.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MySpringBootApplicationTests
{
    @Resource
    MailService mailService;
    @Test
    public void tests() {

    }
}
