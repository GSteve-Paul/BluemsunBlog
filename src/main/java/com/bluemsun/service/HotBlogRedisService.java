package com.bluemsun.service;

import com.bluemsun.dao.BlogDao;
import com.bluemsun.entity.Blog;
import com.bluemsun.entity.Column;
import com.bluemsun.entity.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class HotBlogRedisService
{
    public final static String ColumnLikeKey = "ColumnLikeKey";
    public final static String AllColumnLikeKey = "0" + ColumnLikeKey;
    public final static Long UpLike = 100000000000L;
    @Resource
    StringRedisTemplate redisTemplate4;
    @Resource
    BlogDao blogDao;
    @Resource
    LikeBlogRedisService likeBlogRedisService;

    public String getColumnLikeKey(Long columnId) {
        return columnId + ColumnLikeKey;
    }

    public Long getColumnIdFromKey(String key) {
        return Long.parseLong(key.substring(0, key.indexOf(ColumnLikeKey)));
    }

    public void OutOfDisplay(Long blogId) {
        List<Column> columns = blogDao.getColumnsOfBlog(blogId);
        for (Column column : columns) {
            Long columnId = column.getId();
            String key = getColumnLikeKey(columnId);
            redisTemplate4.opsForZSet().remove(key, blogId.toString());
        }
        redisTemplate4.opsForZSet().remove(AllColumnLikeKey, blogId.toString());
    }

    public void IntoDisplay(Long blogId, List<Long> columnIds) {
        Long score = likeBlogRedisService.getLikeAmount(blogId);
        Blog blog = blogDao.getBlog(blogId, null, null);
        if (blog.getUp() == 1) {
            score += UpLike;
        }
        for (Long columnId : columnIds) {
            String key = getColumnLikeKey(columnId);
            redisTemplate4.opsForZSet().addIfAbsent(key, blogId.toString(), score);
        }
        redisTemplate4.opsForZSet().addIfAbsent(AllColumnLikeKey, blogId.toString(), score);
    }

    public void IntoDisplay(Long blogId) {
        List<Column> columns = blogDao.getColumnsOfBlog(blogId);
        List<Long> columnIds = new ArrayList<>();
        for (Column column : columns) {
            columnIds.add(column.getId());
        }
        IntoDisplay(blogId, columnIds);
    }

    public void increaseLike(Long blogId) {
        List<Column> columns = blogDao.getColumnsOfBlog(blogId);
        for (Column column : columns) {
            Long columnId = column.getId();
            String key = getColumnLikeKey(columnId);
            redisTemplate4.opsForZSet().addIfAbsent(key, blogId.toString(), 0);
            redisTemplate4.opsForZSet().incrementScore(key, blogId.toString(), 1);
        }
        redisTemplate4.opsForZSet().addIfAbsent(AllColumnLikeKey, blogId.toString(), 0);
        redisTemplate4.opsForZSet().incrementScore(AllColumnLikeKey, blogId.toString(), 1);
    }

    public void decreaseLike(Long blogId) {
        List<Column> columns = blogDao.getColumnsOfBlog(blogId);
        for (Column column : columns) {
            Long columnId = column.getId();
            String key = getColumnLikeKey(columnId);
            redisTemplate4.opsForZSet().incrementScore(key, blogId.toString(), -1);
        }
        redisTemplate4.opsForZSet().incrementScore(AllColumnLikeKey, blogId.toString(), -1);
    }

    public void outOfUp(Long blogId) {
        List<Column> columns = blogDao.getColumnsOfBlog(blogId);
        for (Column column : columns) {
            Long columnId = column.getId();
            String key = getColumnLikeKey(columnId);
            redisTemplate4.opsForZSet().incrementScore(key, blogId.toString(), -UpLike);
        }
        redisTemplate4.opsForZSet().incrementScore(AllColumnLikeKey, blogId.toString(), -UpLike);
    }

    public void intoUp(Long blogId) {
        List<Column> columns = blogDao.getColumnsOfBlog(blogId);
        for (Column column : columns) {
            Long columnId = column.getId();
            String key = getColumnLikeKey(columnId);
            redisTemplate4.opsForZSet().incrementScore(key, blogId.toString(), UpLike);
        }
        redisTemplate4.opsForZSet().incrementScore(AllColumnLikeKey, blogId.toString(), UpLike);
    }

    public Long getAmountOfColumn(Long columnId) {
        return redisTemplate4.opsForZSet().size(getColumnLikeKey(columnId));
    }

    public List<Blog> getPageOfColumn(Page<Blog> page, Long columnId) {
        List<Blog> blogList = new ArrayList<>();
        Set<String> set = redisTemplate4.opsForZSet().reverseRange(getColumnLikeKey(columnId), page.getStartIndex(), page.getStartIndex() - 1 + page.getPageSize());
        for (String blogIdString : set) {
            Long blogId = Long.parseLong(blogIdString);
            Blog blog = blogDao.getBlog(blogId,Blog.AUDITED,null);
            blogList.add(blog);
        }
        return blogList;
    }
}
