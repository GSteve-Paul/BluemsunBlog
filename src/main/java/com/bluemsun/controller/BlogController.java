package com.bluemsun.controller;

import com.bluemsun.entity.Blog;
import com.bluemsun.entity.JsonResponse;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.BlogService;
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
@Controller("/blog")
public class BlogController
{
    @Resource
    BlogService blogService;

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
    @TokenChecker({"user", "admin"})
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
    @TokenChecker({"admin"})
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
    @TokenChecker({"user","admin"})
    public JsonResponse setUp(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        Long blogId = jsonNode.get("blogId").asLong();
        Boolean up = jsonNode.get("up").asBoolean();
        blogService.setUp(blogId, up);
        return new JsonResponse(2000, "change the up of blog successfully", null);
    }
/*
    public JsonResponse good() {

    }

    public JsonResponse collect() {

    }
*/
}
