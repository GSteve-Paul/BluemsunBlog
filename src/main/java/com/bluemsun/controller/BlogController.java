package com.bluemsun.controller;

import com.bluemsun.entity.*;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.BlogService;
import com.bluemsun.service.BlogUserCollectService;
import com.bluemsun.service.BlogUserLikeService;
import com.bluemsun.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController
{
    @Resource
    BlogService blogService;
    @Resource
    BlogUserLikeService blogUserLikeService;
    @Resource
    BlogUserCollectService blogUserCollectService;
    @Resource
    UserService userService;


    @PostMapping("/blog")
    @TokenChecker({"user", "admin"})
    public JsonResponse saveBlog(HttpServletRequest request) throws IOException {
        String token = request.getHeader("token");
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        JsonNode blogNode = jsonNode.get("blog");
        JsonNode columnIdsNode = jsonNode.get("columnIds");
        Blog blog = objectMapper.convertValue(blogNode, Blog.class);
        List columnIds = objectMapper.readerFor(new TypeReference<List<Long>>() {}).readValue(columnIdsNode);
        blogService.saveBlog(blog, columnIds, token);
        return new JsonResponse(2000, "save blog successfully", null);
    }

    @GetMapping("/blog")
    public JsonResponse getBlog(@RequestParam Long blogId) {
        return new JsonResponse(2000,
                "get blog successfully",
                blogService.getBlog(blogId));
    }

    @PutMapping("/blog")
    @TokenChecker({"user", "admin"})
    public JsonResponse updateBlog(HttpServletRequest request) throws IOException {
        String token = request.getHeader("token");
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        JsonNode blogNode = jsonNode.get("blog");
        JsonNode columnIdsNode = jsonNode.get("columnIds");
        List columnIds = objectMapper.readerFor(new TypeReference<List<Long>>() {}).readValue(columnIdsNode);
        Blog blog = objectMapper.convertValue(blogNode, Blog.class);
        blogService.editBlog(blog, columnIds, token);
        return new JsonResponse(2000,
                "update blog successfully",
                null);
    }

    @DeleteMapping("/blog")
    @TokenChecker({"user", "admin"})
    public JsonResponse deleteBlog(HttpServletRequest request) throws IOException {
        String token = request.getHeader("token");
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        Long blogId = jsonNode.get("blogId").asLong();
        blogService.deleteBlog(blogId, token);
        return new JsonResponse(2000, "delete blog successfully", null);
    }

    @PutMapping("/audit")
    @TokenChecker("admin")
    public JsonResponse setAudit(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        Long blogId = jsonNode.get("blogId").asLong();
        Boolean audit = jsonNode.get("audit").asBoolean();
        blogService.setAudit(blogId, audit);
        return new JsonResponse(2000, "change the audit of blog successfully", null);
    }

    @PutMapping("/up")
    @TokenChecker("admin")
    public JsonResponse setUp(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        Long blogId = jsonNode.get("blogId").asLong();
        Boolean up = jsonNode.get("up").asBoolean();
        blogService.setUp(blogId, up);
        return new JsonResponse(2000, "change the up of blog successfully", null);
    }

    @PostMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse like(HttpServletRequest request, @RequestBody BlogUserLike blogUserLike) {
        String token = request.getHeader("token");
        Long blogId = blogUserLike.getBlogId();
        Long userId = userService.getIdFromToken(token);
        JsonResponse jsonResponse = new JsonResponse(2000, "like the blog successfully", null);
        Boolean success = blogUserLikeService.like(userId, blogId);
        if (!success) {
            jsonResponse.setCode(2001);
            jsonResponse.setMessage("fail to like the blog");
        }
        return jsonResponse;
    }

    @DeleteMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse disLike(HttpServletRequest request, @RequestBody BlogUserLike blogUserLike) {
        String token = request.getHeader("token");
        Long blogId = blogUserLike.getBlogId();
        Long userId = userService.getIdFromToken(token);
        JsonResponse jsonResponse = new JsonResponse(2000, "cancel the like successfully");
        Boolean success = blogUserLikeService.dislike(userId, blogId);
        if (!success) {
            jsonResponse.setCode(2001);
            jsonResponse.setMessage("fail to cancel");
        }
        return jsonResponse;
    }

    @GetMapping("/like/amount")
    public JsonResponse likeAmount(@RequestParam Long blogId) {
        Long amount = blogUserLikeService.getLikesAmount(blogId);
        JsonResponse jsonResponse = new JsonResponse(2000, "get like amount successfully", amount);
        return jsonResponse;
    }

    @GetMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse isLike(HttpServletRequest request, @RequestParam Long blogId) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        int val = blogUserLikeService.isLike(userId, blogId);
        JsonResponse jsonResponse = new JsonResponse();
        if (val == BlogUserLikeService.INMYSQL
                || val == BlogUserLikeService.INREDISLIKE) {
            jsonResponse.setCode(2000);
            jsonResponse.setMessage("liked");
            return jsonResponse;
        }
        jsonResponse.setCode(2001);
        jsonResponse.setMessage("not liked");
        return jsonResponse;
    }

    @PostMapping("/collect")
    @TokenChecker({"admin", "user"})
    public JsonResponse collect(HttpServletRequest request, @RequestBody BlogUserCollect blogUserCollect) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        Long blogId = blogUserCollect.getBlogId();
        Boolean val = blogUserCollectService.collect(userId, blogId);
        if (!val) {
            return new JsonResponse(2001, "fail to collect");
        }
        return new JsonResponse(2000, "collect successfully");
    }

    @DeleteMapping("/collect")
    @TokenChecker({"admin", "user"})
    public JsonResponse cancelCollect(HttpServletRequest request, @RequestBody BlogUserCollect blogUserCollect) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        Long blogId = blogUserCollect.getBlogId();
        Boolean val = blogUserCollectService.cancelCollect(userId, blogId);
        if (!val) {
            return new JsonResponse(2001, "fail to cancel");
        }
        return new JsonResponse(2000, "cancel collect successfully");
    }

    @GetMapping("/collect/amount")
    @TokenChecker({"admin", "user"})
    public JsonResponse collectAmount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        int val = blogUserCollectService.getAmount(userId);
        return new JsonResponse(2000, "get the amount successfully", val);
    }

    @PostMapping("/collect/page")
    @TokenChecker({"admin", "user"})
    public JsonResponse collectPage(@RequestBody Page<Blog> page,HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        page.init();
        blogUserCollectService.getPage(page,userId);
        return new JsonResponse(2000,"get the page successfully",page.list);
    }

    @GetMapping("/collect")
    @TokenChecker({"user", "admin"})
    public JsonResponse isCollect(HttpServletRequest request, @RequestParam Long blogId) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        Boolean val = blogUserCollectService.isCollect(userId, blogId);
        if (!val) {
            return new JsonResponse(2001, "not collect");
        }
        return new JsonResponse(2000, "collect");
    }
}
