package com.bluemsun.dao;

import com.bluemsun.entity.Blog;
import com.bluemsun.entity.BlogUserCollect;
import com.bluemsun.entity.BlogUserLike;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogUserCollectDao
{
    Integer insertBlogUserCollect(BlogUserCollect blogUserCollect);
    Integer removeBlogUserCollect(BlogUserCollect blogUserCollect);
    BlogUserCollect checkBlogUserCollect(BlogUserCollect blogUserCollect);

    //page
    Integer getBlogUserCollectAmount(Long userId);
    List<Blog> getBlogUserCollectsInPage(Integer start, Integer len,Long userId);
}
