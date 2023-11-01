package com.bluemsun.dao;

import com.bluemsun.entity.BlogUserLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogUserLikeDao
{
    Integer insertBlogUserLike(BlogUserLike blogUserLike);

    Integer removeBlogUserLike(BlogUserLike blogUserLike);

    BlogUserLike checkBlogUserLike(BlogUserLike blogUserLike);

    Integer updateLikes(Long blogId, Long delta);

    Long getBlogUserLikeAmount(Long blogId);
}
