package com.bluemsun.service;

import com.bluemsun.dao.CommentDao;
import com.bluemsun.entity.Comment;
import com.bluemsun.entity.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentService
{
    @Resource
    CommentDao commentDao;
    @Resource
    UserService userService;
    public Boolean insertComment(Comment comment) {
        if(comment.getCommentId() != null && comment.getBlogId() != null) {
            return false;
        }
        int val = commentDao.insertComment(comment);
        return val == 1;
    }

    public Boolean deleteComment(Comment comment,String token) {
        Long userId = userService.getIdFromToken(token);
        Long commentId = comment.getId();
        Comment comment1 = commentDao.getComment(commentId);
        if(!comment1.getUserId().equals(userId)) {
            return false;
        }
        int val = commentDao.deleteComment(commentId);
        return val == 1;
    }
    
    public Comment getComment(Long commentId) {
        return commentDao.getComment(commentId);
    }

    public Integer getCommentAmount(Comment comment) {
        if(comment.getCommentId() != null && comment.getBlogId() != null) {
            return 0;
        }
        Integer val = commentDao.getCommentAmount(comment);
        return val;
    }

    public void getCommentPage(Comment comment, Page<Comment> page) {
        if(comment.getCommentId() != null && comment.getBlogId() != null) {
            return;
        }
        List<Comment> commentList = commentDao.getTheCommentPage(comment,page.getStartIndex(),page.getPageSize());
        page.list = commentList;
    }
}
