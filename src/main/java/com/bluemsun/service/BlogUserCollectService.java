package com.bluemsun.service;

import com.bluemsun.dao.BlogDao;
import com.bluemsun.dao.BlogUserCollectDao;
import com.bluemsun.entity.Blog;
import com.bluemsun.entity.BlogUserCollect;
import com.bluemsun.entity.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogUserCollectService
{
    @Resource
    BlogUserCollectDao blogUserCollectDao;
    @Resource
    BlogDao blogDao;
    @Resource
    BlogService blogService;

    public Boolean collect(Long userId,Long blogId) {
        if(!blogService.isExist(blogId)) {
            return false;
        }
        if(isCollect(userId,blogId)) {
            return false;
        }
        BlogUserCollect blogUserCollect = new BlogUserCollect(blogId,userId);
        blogUserCollectDao.insertBlogUserCollect(blogUserCollect);
        return true;
    }

    public Boolean cancelCollect(Long userId,Long blogId) {
        if(!blogService.isExist(blogId)) {
            return false;
        }
        if(!isCollect(userId,blogId)) {
            return false;
        }
        BlogUserCollect blogUserCollect = new BlogUserCollect(blogId,userId);
        blogUserCollectDao.removeBlogUserCollect(blogUserCollect);
        return true;
    }

    public Boolean isCollect(Long userId,Long blogId) {
        BlogUserCollect blogUserCollect = new BlogUserCollect(blogId,userId);
        blogUserCollect = blogUserCollectDao.checkBlogUserCollect(blogUserCollect);
        return blogUserCollect != null;
    }

    public Integer getAmount(Long userId) {
        return blogUserCollectDao.getBlogUserCollectAmount(userId);
    }

    public void getPage(Page<Blog> page,Long userId) {
        List<Blog> blogs = blogUserCollectDao.getBlogUserCollectsInPage(page.getStartIndex(),page.getPageSize(),userId);
        List<Long> blogIds = new ArrayList<>();
        for(Blog blog:blogs) {
            blogIds.add(blog.getId());
        }
        page.list = blogDao.getBlogs(blogIds,Blog.AUDITED);
    }
}
