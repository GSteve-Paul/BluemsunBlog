package com.bluemsun.controller;

import com.bluemsun.dao.CommentDao;
import com.bluemsun.entity.Comment;
import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.CommentService;
import com.bluemsun.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/blog")
public class CommentController
{
    @Resource
    CommentService commentService;
    @Resource
    UserService userService;
    @PostMapping("/comment")
    @TokenChecker({"admin","user"})
    public JsonResponse add(@RequestBody Comment comment, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        comment.setUserId(userId);
        Boolean val = commentService.insertComment(comment);
        if(!val) {
            return new JsonResponse(2001,"fail to add");
        }
        return new JsonResponse(2000,"add successfully");
    }

    @DeleteMapping("/comment")
    @TokenChecker({"admin","user"})
    public JsonResponse delete(@RequestBody Comment comment,HttpServletRequest request) {
        String token = request.getHeader("token");
        Boolean val = commentService.deleteComment(comment,token);
        if(!val) {
            return new JsonResponse(2001,"fail to delete");
        }
        return new JsonResponse(2000,"delete successfully");
    }

    @GetMapping("/comment")
    public JsonResponse get(@RequestParam Long commentId) {
        return new JsonResponse(2000,"get the comment successfully",commentService.getComment(commentId));
    }

    @GetMapping("/comment/amount")
    public JsonResponse amount(@RequestParam Long upwardId,@RequestParam Integer isBlog) {
        Comment comment = new Comment();
        if(isBlog == 1) {
            comment.setBlogId(upwardId);
        } else {
            comment.setCommentId(upwardId);
        }
        return new JsonResponse(2000,"get the amount successfully",commentService.getCommentAmount(comment));
    }

    @PostMapping("/comment/page")
    public JsonResponse getPage(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        JsonNode commentNode = jsonNode.get("comment");
        JsonNode pageNode = jsonNode.get("page");
        Comment comment = objectMapper.convertValue(commentNode, Comment.class);
        Page<Comment> page = objectMapper.convertValue(pageNode,Page.class);
        page.init();
        commentService.getCommentPage(comment,page);
        return new JsonResponse(2000,"get the page of comment successfully",page.list);
    }
}
