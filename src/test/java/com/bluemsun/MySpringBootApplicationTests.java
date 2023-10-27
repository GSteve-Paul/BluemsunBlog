package com.bluemsun;

import com.bluemsun.dao.BlogUserLikeDao;
import com.bluemsun.entity.Blog;
import com.bluemsun.entity.BlogUserLike;
import com.bluemsun.service.BlogService;
import com.bluemsun.service.BlogUserLikeService;
import com.bluemsun.service.RedisService;
import com.bluemsun.util.UriUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MySpringBootApplicationTests
{
    @Resource
    BlogUserLikeService blogUserLikeService;
    @Resource
    BlogUserLikeDao blogUserLikeDao;

    @Resource
    StringRedisTemplate redisTemplate3;
    @Test
    public void tests() {
        //blogUserLikeService.like(12L,2L);
        //blogUserLikeService.like(8L,2L);
        //blogUserLikeService.like(9L,3L);
        blogUserLikeService.saveInformationFromRedisToMySQL();
    }
}
