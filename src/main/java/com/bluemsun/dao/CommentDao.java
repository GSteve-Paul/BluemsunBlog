package com.bluemsun.dao;

import com.bluemsun.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao
{
    Integer insertComment(Comment comment);

    Integer deleteComment(Long commentId);

    Comment getComment(Long commentId);

    default Boolean isExist(Long commentId) {
        return getComment(commentId) != null;
    }

    //paramater comment means the upward
    Integer getCommentAmount(Comment comment);

    List<Comment> getCommentPage(Long blogId, Long commentId, Integer start, Integer len);

    default List<Comment> getTheCommentPage(Comment comment, Integer start, Integer len) {
        return getCommentPage(comment.getBlogId(), comment.getCommentId()
                , start, len);
    }

}
