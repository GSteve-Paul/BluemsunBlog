package com.bluemsun.dao;

import com.bluemsun.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface BlogDao
{

    Long insertBlog(Blog blog);

    int insertBlogColumn(Long blogId, List<Long> columnIds);

    int deleteBlogColumn(Long blogId);

    int updateBlogDetail(Blog blog);

    int updateBlogAudit(Boolean audit, Long blogId);

    int updateBlogUp(Boolean up, Long blogId);

    List<Blog> getBlogs(List<Long> blogIds, Boolean audit);

    default Blog getBlog(Long blogId, Boolean audit) {
        List<Long> list = new ArrayList<>();
        list.add(blogId);
        return getBlogs(list, audit).get(0);
    }

    Integer isExist(Long blogId);

    int deleteBlog(Long blogId);

    List<Blog> getBlogsInColumnPage(Long columnId, Long userId, Integer start, Integer len);

    int getBlogsInColumnAmount(Long columnId, Long userId);
}
