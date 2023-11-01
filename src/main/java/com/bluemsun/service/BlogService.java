package com.bluemsun.service;

import com.bluemsun.dao.BlogDao;
import com.bluemsun.dao.BlogUserCollectDao;
import com.bluemsun.dao.BlogUserLikeDao;
import com.bluemsun.entity.*;
import com.bluemsun.util.JWTUtil;
import com.bluemsun.util.UriUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
    @Resource
    BlogUserCollectDao blogUserCollectDao;
    @Resource
    HotBlogRedisService hotBlogRedisService;
    @Resource
    LikeBlogRedisService likeBlogRedisService;

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
        Long blogId = blog.getId();
        //insert the blog_column relations into the table
        blogDao.insertBlogColumn(blogId, columnIds);
        //
    }

    @Transactional
    public void editBlog(Blog blog, List<Long> columnIds, String token) {
        //check uuid
        Blog trueBlog = blogDao.getBlog(blog.getId(), null, null);
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
        hotBlogRedisService.OutOfDisplay(blog.getId());
        blogDao.deleteBlogColumn(blog.getId());
        blogDao.insertBlogColumn(blog.getId(), columnIds);
        //make the blog unaudited
        blogDao.updateBlogAudit(Blog.NOTAUDITED, blog.getId());
    }

    public Blog getBlog(Long blogId, String token) {
        if (token == null) {
            return blogDao.getBlog(blogId, Blog.AUDITED, null);
        }
        Claims claims = jwtUtil.checkToken(token, "admin");
        if (claims != null) {
            return blogDao.getBlog(blogId, null, null);
        } else {
            return blogDao.getBlog(blogId, Blog.AUDITED, null);
        }
    }

    @Transactional
    public void deleteBlog(Long blogId, String token) {
        //check uuid
        long tokenId = userService.getIdFromToken(token);
        Blog blog = blogDao.getBlog(blogId, null, null);
        long blogAuthor = blog.getUserId();
        //is admin
        Boolean isAdmin = jwtUtil.checkToken(token, "admin") != null;
        if (tokenId != blogAuthor && !isAdmin) {
            log.error("token -> userId != blog.userId");
            throw new RuntimeException();
        }
        //delete the .blog file
        String filePath = blog.getContent();
        filePath = UriUtil.serverToLocal(filePath);
        File file = new File(filePath);
        if (!file.delete()) {
            log.error("unable to delete the .blog file");
            throw new RuntimeException();
        }
        //delete blog in Redis
        hotBlogRedisService.OutOfDisplay(blogId);
        //delete blog in MySQL
        blogDao.deleteBlog(blogId);
        log.info("delete blog " + blogId + " successfully");
    }

    public void setAudit(Long blogId, Integer audit) {
        if (audit == 1) {
            hotBlogRedisService.IntoDisplay(blogId);
        } else {
            hotBlogRedisService.OutOfDisplay(blogId);
        }
        blogDao.updateBlogAudit(audit, blogId);
    }

    @Transactional
    public void setUp(Long blogId, Integer up) {
        blogDao.getBlog(blogId, Blog.AUDITED, null);
        blogDao.updateBlogUp(up, blogId);

        if(up == 1) {
            hotBlogRedisService.intoUp(blogId);
        } else {
            hotBlogRedisService.outOfUp(blogId);
        }
    }

    @Transactional
    public Boolean collect(Long userId, Long blogId) {
        if (!isExist(blogId)) {
            return false;
        }
        if (isCollect(userId, blogId)) {
            return false;
        }
        BlogUserCollect blogUserCollect = new BlogUserCollect(blogId, userId);
        blogUserCollectDao.insertBlogUserCollect(blogUserCollect);
        blogUserCollectDao.updateBlogCollectAmount(blogId, 1);
        return true;
    }

    @Transactional
    public Boolean cancelCollect(Long userId, Long blogId) {
        if (!isExist(blogId)) {
            return false;
        }
        if (!isCollect(userId, blogId)) {
            return false;
        }
        BlogUserCollect blogUserCollect = new BlogUserCollect(blogId, userId);
        blogUserCollectDao.removeBlogUserCollect(blogUserCollect);
        blogUserCollectDao.updateBlogCollectAmount(blogId, -1);
        return true;
    }

    public Boolean isCollect(Long userId, Long blogId) {
        BlogUserCollect blogUserCollect = new BlogUserCollect(blogId, userId);
        blogUserCollect = blogUserCollectDao.checkBlogUserCollect(blogUserCollect);
        return blogUserCollect != null;
    }

    public Integer getCollectAmount(Long blogId) {
        return blogUserCollectDao.getBlogCollectAmount(blogId);
    }

    public Boolean like(Long userId, Long blogId) {
        if (!isExist(blogId)) {
            return false;
        }
        boolean val = likeBlogRedisService.increaseLike(blogId,userId);
        if(!val) {
            return false;
        }
        hotBlogRedisService.increaseLike(blogId);
        return true;
    }

    public Boolean cancelLike(Long userId, Long blogId) {
        if (!isExist(blogId)) {
            return false;
        }
        boolean val = likeBlogRedisService.decreaseLike(blogId,userId);
        if(!val) {
            return false;
        }
        hotBlogRedisService.decreaseLike(blogId);
        return true;
    }

    public Long getLikesAmount(Long blogId) {
        return likeBlogRedisService.getLikeAmount(blogId);
    }

    public Integer isLike(Long userId, Long blogId) {
        return likeBlogRedisService.isLike(blogId,userId);
    }

    public Integer getBlogsTimeOrderAmount(Long userId, String token, String key) {
        if(key == null) {
            key = "";
        }
        key = "%" + key + "%";
        Long tokenUserId = userService.getIdFromToken(token);
        return blogDao.getBlogsTimeOrderAmount(userId, tokenUserId, key);
    }

    public void getBlogsTimeOrderPage(Page<Blog> page, Long userId, String token, String key) {
        if(key == null) {
            key = "";
        }
        key = "%" + key + "%";
        Long tokenUserId = userService.getIdFromToken(token);
        page.list = blogDao.getBlogsTimeOrderPage(userId
                , tokenUserId
                , key
                , page.getStartIndex()
                , page.getPageSize());
    }

    public List<Blog> getBlogsLikeOrder(Integer amount, Long columnId) {
        return hotBlogRedisService.getPrefix(amount,columnId);
    }
}
