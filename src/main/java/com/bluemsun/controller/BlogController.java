package com.bluemsun.controller;

import com.bluemsun.entity.*;
import com.bluemsun.interceptor.BanChecker;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.BlogService;
import com.bluemsun.service.LikeBlogRedisService;
import com.bluemsun.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    UserService userService;


    @PostMapping("/blog")
    @TokenChecker({"user", "admin"})
    @BanChecker
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
    public JsonResponse getBlog(@RequestParam Long blogId, HttpServletRequest request) {
        String token = request.getHeader("token");
        return new JsonResponse(2000,
                "get blog successfully",
                blogService.getBlog(blogId, token));
    }

    @PutMapping("/blog")
    @TokenChecker({"user", "admin"})
    @BanChecker
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
        Integer audit = jsonNode.get("audit").asInt();
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
        Integer up = jsonNode.get("up").asInt();
        blogService.setUp(blogId, up);
        return new JsonResponse(2000, "change the up of blog successfully", null);
    }

    @PostMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse like(HttpServletRequest request, @RequestBody BlogUserLike blogUserLike) {
        String token = request.getHeader("token");
        Long blogId = blogUserLike.getBlogId();
        Long userId = userService.getIdFromToken(token);
        if (blogUserLike.getState() == 1) {
            JsonResponse jsonResponse = new JsonResponse(2000, "like the blog successfully", null);
            Boolean success = blogService.like(userId, blogId);
            if (!success) {
                jsonResponse.setCode(2001);
                jsonResponse.setMessage("fail to like the blog");
            }
            return jsonResponse;
        } else {
            JsonResponse jsonResponse = new JsonResponse(2000, "cancel the like successfully");
            Boolean success = blogService.cancelLike(userId, blogId);
            if (!success) {
                jsonResponse.setCode(2001);
                jsonResponse.setMessage("fail to cancel");
            }
            return jsonResponse;
        }
    }

    @GetMapping("/like/amount")
    public JsonResponse likeAmount(@RequestParam Long blogId) {
        Long amount = blogService.getLikesAmount(blogId);
        JsonResponse jsonResponse = new JsonResponse(2000, "get like amount successfully", amount);
        return jsonResponse;
    }

    @GetMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse isLike(HttpServletRequest request, @RequestParam Long blogId) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        int val = blogService.isLike(userId, blogId);
        JsonResponse jsonResponse = new JsonResponse();
        if (val == LikeBlogRedisService.INMYSQL
                || val == LikeBlogRedisService.INREDISLIKE) {
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
        if (blogUserCollect.getState() == 1) {
            Boolean val = blogService.collect(userId, blogId);
            if (!val) {
                return new JsonResponse(2001, "fail to collect");
            }
            return new JsonResponse(2000, "collect successfully");
        } else {
            Boolean val = blogService.cancelCollect(userId, blogId);
            if (!val) {
                return new JsonResponse(2001, "fail to cancel");
            }
            return new JsonResponse(2000, "cancel collect successfully");
        }
    }

    @GetMapping("/collect/amount")
    public JsonResponse collectAmount(HttpServletRequest request, @RequestParam Long blogId) {
        int val = blogService.getCollectAmount(blogId);
        return new JsonResponse(2000, "get the amount successfully", val);
    }

    @GetMapping("/collect")
    @TokenChecker({"user", "admin"})
    public JsonResponse isCollect(HttpServletRequest request, @RequestParam Long blogId) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        Boolean val = blogService.isCollect(userId, blogId);
        if (!val) {
            return new JsonResponse(2001, "not collect");
        }
        return new JsonResponse(2000, "collect");
    }

    @PostMapping("/order/like")
    public JsonResponse getBlogsLikeOrder(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        Integer amount = jsonNode.get("amount").asInt();
        Long columnId = jsonNode.get("columnId").asLong();
        List<Blog> blogList = blogService.getBlogsLikeOrder(amount, columnId);
        return new JsonResponse(2000, "get blogs in like order successfully", blogList);
    }

    @GetMapping("/order/time")
    @TokenChecker({"admin", "user"})
    public JsonResponse getBlogsTimeOrderAmount(@RequestParam(required = false) Long userId
                                                , @RequestParam(required = false) String key
                                                , HttpServletRequest request) {
        String token = request.getHeader("token");
        Integer val = blogService.getBlogsTimeOrderAmount(userId, token, key);
        return new JsonResponse(2000, "get the amount of blogs in time order successfully", val);
    }

    @PostMapping("/order/time")
    @TokenChecker({"admin", "user"})
    public JsonResponse getBlogsTimeOrderPage(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        Page<Blog> page = objectMapper.convertValue(jsonNode.get("page"), Page.class);
        page.init();
        String token = request.getHeader("token");
        Long userId = objectMapper.convertValue(jsonNode.get("userId"), Long.class);
        String key = objectMapper.convertValue(jsonNode.get("key"), String.class);
        blogService.getBlogsTimeOrderPage(page, userId, token, key);
        return new JsonResponse(2000, "get blogs in time order successfully", page.list);
    }
}
