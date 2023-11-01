package com.bluemsun.dao;

import com.bluemsun.entity.CommentUserLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentUserLikeDao
{
    Integer insertCommentUserLike(CommentUserLike commentUserLike);

    Integer removeCommentUserLike(CommentUserLike commentUserLike);

    CommentUserLike checkCommentUserLike(CommentUserLike commentUserLike);

    Integer updateLikes(Long commentId, Long delta);

    Long getCommentUserLikeAmount(Long commentId);
}
