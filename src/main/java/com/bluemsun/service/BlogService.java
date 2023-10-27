package com.bluemsun.service;

import com.bluemsun.dao.BlogDao;
import com.bluemsun.entity.Blog;
import com.bluemsun.util.JWTUtil;
import com.bluemsun.util.UriUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BlogService
{
    @Resource
    JWTUtil jwtUtil;
    @Resource
    BlogDao blogDao;
    @Resource
    UserService userService;

    public Boolean isExist(Long blogId) {
        int val = blogDao.isExist(blogId);
        return val == 1;
    }

    @Transactional
    public void saveBlog(Blog blog, List<Long> columnIds, String token) {
        //save the content into a new file
        blog.setUserId(userService.getIdFromToken(token));
        String title = blog.getTitle();
        String fileName = UUID.randomUUID() + title + ".blog";
        String filePath = UriUtil.getLocalUri(fileName);
        String content = blog.getContent();
        try {
            File file = new File(filePath).getParentFile();
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(filePath);
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            log.error("IOException in saving the blog");
            throw new RuntimeException();
        }
        //replace the content with the path of the corresponding file
        String fileServerPath = UriUtil.getServerUri(fileName);
        blog.setContent(fileServerPath);
        //insert data into the blog table
        blogDao.insertBlog(blog);
        //insert the blog_column relations into the table
        blogDao.insertBlogColumn(blog.getId(), columnIds);
    }

    @Transactional
    public void editBlog(Blog blog, List<Long> columnIds, String token) {
        //check uuid
        Blog trueBlog = blogDao.getBlog(blog.getId(),Blog.AUDITED);
        long tokenId = userService.getIdFromToken(token);
        long blogAuthor = trueBlog.getUserId();
        if (tokenId != blogAuthor) {
            log.error("token -> userId != blog.userId");
            throw new RuntimeException();
        }
        //save the new content into the existing file
        String content = blog.getContent();
        String filePath = trueBlog.getContent();
        filePath = UriUtil.serverToLocal(filePath);
        try {
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            log.error("IOException in saving the blog");
            throw new RuntimeException();
        }
        //update the detail of the blog
        blog.setContent(trueBlog.getContent());
        blogDao.updateBlogDetail(blog);
        //edit the blog_column relationship
        blogDao.deleteBlogColumn(blog.getId());
        blogDao.insertBlogColumn(blog.getId(), columnIds);
        //make the blog unaudited
        blogDao.updateBlogAudit(Blog.NOTAUDITED, blog.getId());
    }

    public Blog getBlog(Long blogId) {
        return blogDao.getBlog(blogId,Blog.AUDITED);
    }

    @Transactional
    public void deleteBlog(Long blogId, String token) {
        //check uuid
        long tokenId = userService.getIdFromToken(token);
        Blog blog =  blogDao.getBlog(blogId,null);
        long blogAuthor = blog.getUserId();
        if (tokenId != blogAuthor) {
            log.error("token -> userId != blog.userId");
            throw new RuntimeException();
        }
        //delete the .blog file
        String filePath = blog.getContent();
        filePath = UriUtil.serverToLocal(filePath);
        File file = new File(filePath);
        if(!file.delete()) {
            log.error("unable to delete the .blog file");
            throw new RuntimeException();
        }
        //delete blog in MySQL
        blogDao.deleteBlog(blogId);
    }

    public void setAudit(Long blogId, Boolean audit) {
        blogDao.updateBlogAudit(audit, blogId);
    }

    public void setUp(Long blogId, Boolean up) {
        blogDao.updateBlogUp(up, blogId);
    }

}
