package com.bluemsun.service;

import com.bluemsun.dao.BlogUserLikeDao;
import com.bluemsun.entity.BlogUserLike;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class BlogUserLikeService
{
    public final static Integer INMYSQL = 2;
    public final static Integer INREDISLIKE = 1;
    public final static Integer NOTEXIST = 0;
    public final static Integer INREDISDISLIKE = -1;
    @Resource
    StringRedisTemplate redisTemplate3;
    @Resource
    BlogUserLikeDao blogUserLikeDao;
    @Resource
    BlogService blogService;

    public final static String LikeKey = "BlogLike";
    public final static String CntKey = "BlogLikeCnt";

    String getLikeKey(Long blogId) {
        return blogId + "BlogLike";
    }

    String getCntKey(Long blogId) {
        return blogId + "BlogLikeCnt";
    }

    Long getBlogId(String key) {
        String val = key.substring(0,key.indexOf(LikeKey));
        return Long.parseLong(val);
    }

    public Boolean like(Long userId, Long blogId) {
        if(!blogService.isExist(blogId)) {
            return false;
        }
        int value = isLike(userId, blogId);
        if (value == INMYSQL || value == INREDISLIKE) {
            return false;
        }
        redisTemplate3.opsForValue().setIfAbsent
                (getCntKey(blogId), String.valueOf(0));
        redisTemplate3.opsForValue().increment(getCntKey(blogId));
        if (value == INREDISDISLIKE) {
            deleteLike(userId, blogId);
            return true;
        }
        redisTemplate3.opsForHash().put(getLikeKey(blogId)
                , userId.toString(), "1");

        return true;
    }

    public Boolean dislike(Long userId, Long blogId) {
        if(!blogService.isExist(blogId)) {
            return false;
        }
        int value = isLike(userId, blogId);
        if (value == INREDISDISLIKE || value == NOTEXIST) {
            return false;
        }
        redisTemplate3.opsForValue().decrement(getCntKey(blogId));
        if (value == INREDISLIKE) {
            deleteLike(userId, blogId);
            return true;
        }
        redisTemplate3.opsForHash().put(getLikeKey(blogId)
                , userId.toString(), "0");
        return true;
    }

    public void deleteLike(Long userId, Long blogId) {
        redisTemplate3.opsForHash().delete(getLikeKey(blogId)
                , userId.toString());
    }

    public Long getLikesAmount(Long blogId) {
        Long val = blogUserLikeDao.getBlogUserLikeAmount(blogId);
        try {
            return val + Long.parseLong(redisTemplate3.opsForValue().get(getCntKey(blogId)));
        } catch (NullPointerException ex) {
            return val;
        }
    }

    public Integer isLike(Long userId, Long blogId) {
        if (!redisTemplate3.opsForHash()
                .hasKey(getLikeKey(blogId), userId.toString())) {
            BlogUserLike blogUserLike = blogUserLikeDao
                    .checkBlogUserLike(new BlogUserLike(userId, blogId));
            if (blogUserLike != null) {
                return INMYSQL;
            }
            return NOTEXIST;
        }
        String value = redisTemplate3.opsForHash().get(getLikeKey(blogId)
                , userId.toString()).toString();
        if ("1".equals(value)) {
            return INREDISLIKE;
        }
        return INREDISDISLIKE;
    }

    public void saveInformationFromRedisToMySQL() {
        //save BLogUserLike
        ScanOptions options = ScanOptions.scanOptions()
                .match("*" + LikeKey)
                .count(10)
                .build();
        Cursor<String> cursor = redisTemplate3.scan(options);
        while (cursor.hasNext()) {
            String hashName = cursor.next();
            Long blogId = getBlogId(hashName);
            Cursor<Map.Entry<Object, Object>> hashCursor =
                    redisTemplate3.opsForHash().scan(hashName, ScanOptions.NONE);
            while (hashCursor.hasNext()) {
                Map.Entry<Object, Object> entry = hashCursor.next();
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                BlogUserLike blogUserLike = new BlogUserLike(Long.parseLong(key), blogId);
                if (value.equals("1")) {
                    blogUserLikeDao.insertBlogUserLike(blogUserLike);
                } else {
                    blogUserLikeDao.removeBlogUserLike(blogUserLike);
                }
            }
            hashCursor.close();
        }
        cursor.close();
        //save likes in Blog
        ScanOptions countOptions = ScanOptions.scanOptions()
                .match("*" + CntKey)
                .count(10)
                .build();
        Cursor<String> countCursor = redisTemplate3.scan(countOptions);
        while(countCursor.hasNext()) {
            String key = countCursor.next();
            Long blogId = getBlogId(key);
            Long delta = Long.parseLong(redisTemplate3.opsForValue().get(key));
            blogUserLikeDao.updateLikes(blogId,delta);
        }
    }
}
