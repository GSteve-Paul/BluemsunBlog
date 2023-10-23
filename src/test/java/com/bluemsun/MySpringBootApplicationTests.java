package com.bluemsun;

import com.bluemsun.entity.Blog;
import com.bluemsun.service.BlogService;
import com.bluemsun.service.RedisService;
import com.bluemsun.util.UriUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MySpringBootApplicationTests
{
    @Test
    public void tests() {
        String tmp = UriUtil.serverToLocal("http://127.0.0.1:8081/static/18af56a3-2fe8-49ae-82fa-5fdf87428c1bmy first title.blog");
        System.out.println(tmp);
    }
}
