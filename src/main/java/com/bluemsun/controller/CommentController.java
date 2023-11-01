package com.bluemsun.controller;

import com.bluemsun.entity.Comment;
import com.bluemsun.entity.CommentUserLike;
import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.interceptor.BanChecker;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.CommentService;
import com.bluemsun.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/blog/comment")
public class CommentController
{
    @Resource
    CommentService commentService;
    @Resource
    UserService userService;

    @PostMapping("")
    @TokenChecker({"admin", "user"})
    @BanChecker
    public JsonResponse add(@RequestBody Comment comment, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        comment.setUserId(userId);
        Boolean val = commentService.insertComment(comment);
        if (!val) {
            return new JsonResponse(2001, "fail to add");
        }
        return new JsonResponse(2000, "add successfully");
    }

    @DeleteMapping("")
    @TokenChecker({"admin", "user"})
    public JsonResponse delete(@RequestBody Comment comment, HttpServletRequest request) {
        String token = request.getHeader("token");
        Boolean val = commentService.deleteComment(comment, token);
        if (!val) {
            return new JsonResponse(2001, "fail to delete");
        }
        return new JsonResponse(2000, "delete successfully");
    }

    @GetMapping("")
    public JsonResponse get(@RequestParam Long commentId) {
        return new JsonResponse(2000, "get the comment successfully", commentService.getComment(commentId));
    }

    @GetMapping("/amount")
    public JsonResponse amount(@RequestParam Long upwardId, @RequestParam Integer isBlog) {
        Comment comment = new Comment();
        if (isBlog == 1) {
            comment.setBlogId(upwardId);
        } else {
            comment.setCommentId(upwardId);
        }
        return new JsonResponse(2000, "get the amount successfully", commentService.getCommentAmount(comment));
    }

    @PostMapping("/page")
    public JsonResponse getPage(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        JsonNode commentNode = jsonNode.get("comment");
        JsonNode pageNode = jsonNode.get("page");
        Comment comment = objectMapper.convertValue(commentNode, Comment.class);
        Page<Comment> page = objectMapper.convertValue(pageNode, Page.class);
        page.init();
        commentService.getCommentPage(comment, page);
        return new JsonResponse(2000, "get the page of comment successfully", page.list);
    }

    @PostMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse like(HttpServletRequest request, @RequestBody CommentUserLike commentUserLike) {
        String token = request.getHeader("token");
        Long commentId = commentUserLike.getCommentId();
        Long userId = userService.getIdFromToken(token);
        if (commentUserLike.getState() == 1) {
            JsonResponse jsonResponse = new JsonResponse(2000, "like the comment successfully", null);
            Boolean success = commentService.like(userId, commentId);
            if (!success) {
                jsonResponse.setCode(2001);
                jsonResponse.setMessage("fail to like the comment");
            }
            return jsonResponse;
        }
        JsonResponse jsonResponse = new JsonResponse(2000, "cancel the like successfully", null);
        Boolean success = commentService.disLike(userId, commentId);
        if (!success) {
            jsonResponse.setCode(2001);
            jsonResponse.setMessage("fail to cancel");
        }
        return jsonResponse;
    }

    @GetMapping("/like")
    @TokenChecker({"admin", "user"})
    public JsonResponse isLike(HttpServletRequest request, @RequestParam Long commentId) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        int val = commentService.isLike(userId, commentId);
        JsonResponse jsonResponse = new JsonResponse();
        if (val == CommentService.INMYSQL || val == CommentService.INREDISLIKE) {
            jsonResponse.setCode(2000);
            jsonResponse.setMessage("liked");
            return jsonResponse;
        }
        jsonResponse.setCode(2001);
        jsonResponse.setMessage("not liked");
        return jsonResponse;
    }

    @GetMapping("/like/amount")
    public JsonResponse likeAmount(@RequestParam Long commentId) {
        Long amount = commentService.getLikesAmount(commentId);
        if(amount == null) {
            amount = 0L;
        }
        return new JsonResponse(2000, "get like amount successfully", amount);
    }
}
