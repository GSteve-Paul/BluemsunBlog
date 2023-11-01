package com.bluemsun.service;

import com.bluemsun.dao.CommentDao;
import com.bluemsun.dao.CommentUserLikeDao;
import com.bluemsun.entity.Comment;
import com.bluemsun.entity.CommentUserLike;
import com.bluemsun.entity.Page;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class CommentService
{

    public final static Integer INMYSQL = 2;
    public final static Integer INREDISLIKE = 1;
    public final static Integer NOTEXIST = 0;
    public final static Integer INREDISDISLIKE = -1;
    public final static String LikeKey = "CommentLike";
    public final static String CntKey = "CommentLikeCnt";
    @Resource
    CommentDao commentDao;
    @Resource
    CommentUserLikeDao commentUserLikeDao;
    @Resource
    UserService userService;
    @Resource
    StringRedisTemplate redisTemplate3;

    public Boolean insertComment(Comment comment) {
        if (comment.getCommentId() != null && comment.getBlogId() != null) {
            return false;
        }
        int val = commentDao.insertComment(comment);
        return val == 1;
    }

    public Boolean deleteComment(Comment comment, String token) {
        Long userId = userService.getIdFromToken(token);
        Long commentId = comment.getId();
        Comment comment1 = commentDao.getComment(commentId);
        if (!comment1.getUserId().equals(userId)) {
            return false;
        }
        int val = commentDao.deleteComment(commentId);
        return val == 1;
    }

    public Comment getComment(Long commentId) {
        return commentDao.getComment(commentId);
    }

    public Integer getCommentAmount(Comment comment) {
        if (comment.getCommentId() != null && comment.getBlogId() != null) {
            return 0;
        }
        return commentDao.getCommentAmount(comment);
    }

    public void getCommentPage(Comment comment, Page<Comment> page) {
        if (comment.getCommentId() != null && comment.getBlogId() != null) {
            return;
        }
        page.list = commentDao.getTheCommentPage(comment, page.getStartIndex(), page.getPageSize());
    }

    String getLikeKey(Long commentId) {return commentId + LikeKey;}

    String getCntKey(Long commentId) {return commentId + CntKey;}

    Long getCommentId(String key) {
        String val = key.substring(0, key.indexOf(LikeKey));
        return Long.parseLong(val);
    }

    public Boolean like(Long userId, Long commentId) {
        if (!commentDao.isExist(commentId)) {
            return false;
        }
        int val = isLike(userId, commentId);
        if (val == INMYSQL || val == INREDISLIKE) {
            return false;
        }
        redisTemplate3.opsForValue().setIfAbsent(getCntKey(commentId), "0");
        redisTemplate3.opsForValue().increment(getCntKey(commentId));
        if (val == INREDISDISLIKE) {
            deleteLike(userId, commentId);
            return true;
        }
        redisTemplate3.opsForHash().put(getLikeKey(commentId), userId.toString(), "1");
        return true;
    }

    public Boolean disLike(Long userId, Long commentId) {
        if (!commentDao.isExist(commentId)) {
            return false;
        }
        int value = isLike(userId, commentId);
        if (value == INREDISDISLIKE || value == NOTEXIST) {
            return false;
        }
        redisTemplate3.opsForValue().decrement(getCntKey(commentId));
        if (value == INREDISLIKE) {
            deleteLike(userId, commentId);
            return true;
        }
        redisTemplate3.opsForHash().put(getLikeKey(commentId), userId.toString(), "0");
        return true;
    }

    public void deleteLike(Long userId, Long commentId) {
        redisTemplate3.opsForHash().delete(getLikeKey(commentId), userId.toString());
    }

    public Long getLikesAmount(Long commentId) {
        Long val = commentUserLikeDao.getCommentUserLikeAmount(commentId);
        try {
            return val + Long.parseLong(redisTemplate3.opsForValue().get(getCntKey(commentId)));
        } catch (Exception ex) {
            return val;
        }
    }

    public Integer isLike(Long userId, Long commentId) {
        if (!redisTemplate3.opsForHash().hasKey(getLikeKey(commentId), userId.toString())) {
            CommentUserLike commentUserLike = commentUserLikeDao.checkCommentUserLike(new CommentUserLike(commentId, userId));
            if (commentUserLike != null) {
                return INMYSQL;
            }
            return NOTEXIST;
        }
        String value = redisTemplate3.opsForHash().get(getLikeKey(commentId), userId.toString()).toString();
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
            Long commentId = getCommentId(hashName);
            Cursor<Map.Entry<Object, Object>> hashCursor = redisTemplate3.opsForHash().scan(hashName, ScanOptions.NONE);
            while (hashCursor.hasNext()) {
                Map.Entry<Object, Object> entry = hashCursor.next();
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                CommentUserLike commentUserLike = new CommentUserLike(commentId, Long.parseLong(key));
                if (value.equals("1")) {
                    commentUserLikeDao.insertCommentUserLike(commentUserLike);
                } else {
                    commentUserLikeDao.removeCommentUserLike(commentUserLike);
                }
            }
            redisTemplate3.delete(hashName);
            hashCursor.close();
        }
        cursor.close();
        //save likes in Comment
        ScanOptions countOptions = ScanOptions.scanOptions()
                .match("*" + CntKey)
                .count(10)
                .build();
        Cursor<String> countCursor = redisTemplate3.scan(countOptions);
        while (countCursor.hasNext()) {
            String key = countCursor.next();
            Long commentId = getCommentId(key);
            Long delta = Long.parseLong(redisTemplate3.opsForValue().get(key));
            commentUserLikeDao.updateLikes(commentId, delta);
            redisTemplate3.delete(key);
        }
        countCursor.close();
    }

}
