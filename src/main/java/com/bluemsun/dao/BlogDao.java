package com.bluemsun.dao;

import com.bluemsun.entity.Blog;
import com.bluemsun.entity.Column;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface BlogDao
{

    Long insertBlog(Blog blog);

    int insertBlogColumn(Long blogId, List<Long> columnIds);

    int deleteBlogColumn(Long blogId);

    List<Column> getColumnsOfBlog(Long blogId);

    int updateBlogDetail(Blog blog);

    int updateBlogAudit(Integer audit, Long blogId);

    int updateBlogUp(Integer up, Long blogId);

    List<Blog> getBlogs(List<Long> blogIds, Integer audit, Integer up);

    default Blog getBlog(Long blogId, Integer audit,Integer up) {
        List<Long> list = new ArrayList<>();
        list.add(blogId);
        return getBlogs(list, audit, up).get(0);
    }

    Integer isExist(Long blogId);

    int deleteBlog(Long blogId);

    List<Blog> getBlogsTimeOrderPage(Long userId, Long tokenUserId, String key, Integer start, Integer len);

    Integer getBlogsTimeOrderAmount(Long userId, Long tokenUserId, String key);

    List<Blog> getUpBlog(int start, int len);

}
