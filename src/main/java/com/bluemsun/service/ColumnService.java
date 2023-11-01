package com.bluemsun.service;

import com.bluemsun.dao.BlogDao;
import com.bluemsun.dao.ColumnDao;
import com.bluemsun.entity.Blog;
import com.bluemsun.entity.Column;
import com.bluemsun.entity.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ColumnService
{
    @Resource
    UserService userService;
    @Resource
    BlogDao blogDao;
    @Resource
    ColumnDao columnDao;

    public Boolean insertColumn(Column column) {
        columnDao.insertColumn(column);
        return true;
    }

    public Column getColumn(Long columnId) {
        return columnDao.getColumn(columnId);
    }

    public Boolean updateColumn(Column column) {
        columnDao.updateColumn(column);
        return true;
    }

    public List<Column> getAllColumn() {
        return columnDao.getAllColumns();
    }

    public Integer getBlogAmount(Long columnId) {
        return columnDao.getBlogAmount(columnId);
    }

    public void getBlogsInColumnPage(Long columnId, Page<Blog> page) {
        List<Long> blogIds = columnDao.getBlogsInColumnPage(
                columnId,
                page.getStartIndex(),
                page.getPageSize());
        List<Blog> blogs = blogDao.getBlogs(blogIds, Blog.AUDITED, null);
        page.list = blogs;
    }
}
