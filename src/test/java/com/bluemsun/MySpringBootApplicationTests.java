package com.bluemsun;

import com.bluemsun.service.CommentService;
import com.bluemsun.service.HotBlogRedisService;
import com.bluemsun.service.LikeBlogRedisService;
import com.bluemsun.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MySpringBootApplicationTests
{
    @Resource
    CommentService commentService;
    @Resource
    LikeBlogRedisService likeBlogRedisService;
    @Test
    public void tests() {

    }
}
