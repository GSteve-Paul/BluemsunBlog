package com.bluemsun.controller;

import com.bluemsun.entity.Blog;
import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.entity.User;
import com.bluemsun.interceptor.LoginChecker;
import com.bluemsun.interceptor.RegisterChecker;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.BlogService;
import com.bluemsun.service.UserService;
import com.bluemsun.util.JWTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/user")
public class UserController
{
    @Resource
    JWTUtil jwtUtil;
    @Resource
    UserService userService;
    @Resource
    BlogService blogService;

    @PostMapping("/register")
    @RegisterChecker
    public JsonResponse register(@RequestBody User user) {
        JsonResponse jsonResponse = new JsonResponse();
        Long uuid = userService.registerUser(user.getName(), user.getPwd(), user.getPhone(), user.getAdmin());
        if (uuid == 0L) {
            jsonResponse.setCode(2001);
            jsonResponse.setMessage("register fail");
            jsonResponse.setData("");
        } else {
            jsonResponse.setCode(2000);
            jsonResponse.setMessage("register successfully");
            jsonResponse.setData(uuid);
        }
        return jsonResponse;
    }

    @PostMapping("/login")
    @LoginChecker
    public JsonResponse login(@RequestBody User user) {
        JsonResponse jsonResponse = new JsonResponse();
        String token = userService.loginUser(user.getUuid(), user.getPwd(), user.getAdmin());
        if (token != null) {
            jsonResponse.setCode(2000);
            jsonResponse.setMessage("Login successfully");
            jsonResponse.setData(token);
        } else {
            jsonResponse.setCode(2002);
            jsonResponse.setMessage("Login failed");
        }
        return jsonResponse;
    }

    @DeleteMapping("/logout")
    @TokenChecker({"user", "admin"})
    public JsonResponse logout(HttpServletRequest request) {
        String token = jwtUtil.getToken(request);
        userService.logout(token);
        return new JsonResponse(2000, "Logout successfully");
    }

    @GetMapping("/info")
    @TokenChecker({"user", "admin"})
    public JsonResponse getInfo(HttpServletRequest request,@RequestParam Long userId) {
        String token = jwtUtil.getToken(request);
        if(userId == 0L) {
            userId = userService.getIdFromToken(token);
        }
        User user = userService.getInfo(userId, false);
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setCode(2000);
        jsonResponse.setMessage("Get info successfully");
        jsonResponse.setData(user);
        return jsonResponse;
    }

    @PutMapping("/info")
    @TokenChecker({"user", "admin"})
    public JsonResponse putInfo(@RequestBody User user, HttpServletRequest request) {
        String token = jwtUtil.getToken(request);
        Long userId = userService.getIdFromToken(token);
        user.setId(userId);
        int val = userService.updateInfo(user);
        JsonResponse jsonResponse = new JsonResponse(2000, "update successfully", null);
        if (val == 0) {
            jsonResponse = new JsonResponse(2001, "unknown error", null);
        }
        return jsonResponse;
    }

    @PutMapping("/pwd")
    @TokenChecker({"user", "admin"})
    public JsonResponse putPwd(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        JsonNode oldPwdNode = jsonNode.get("oldPwd");
        JsonNode newPwdNode = jsonNode.get("newPwd");
        String oldPwd = objectMapper.convertValue(oldPwdNode, String.class);
        String newPwd = objectMapper.convertValue(newPwdNode, String.class);
        String token = request.getHeader("token");
        int val = userService.updatePwd(token, oldPwd, newPwd);
        userService.logout(token);
        switch (val) {
            case 0: return new JsonResponse(2000, "update the password successfully");
            case 1: return new JsonResponse(2001, "wrong old password");
            case 2: return new JsonResponse(2002, "empty password");
            case 3: return new JsonResponse(2003, "spaces exist in password");
            case 4: return new JsonResponse(2004, "too long password");
            case 5: return new JsonResponse(2005, "password is not complex enough");
        }
        return new JsonResponse(2006, "unknown error");
    }

    @GetMapping("/amount")
    public JsonResponse getAmount() {
        int amount = userService.getAmount();
        return new JsonResponse(2000, "获取成功", amount);
    }

    @PostMapping("/page")
    public JsonResponse getPage(@RequestBody Page<User> page) {
        page.init();
        userService.getPage(page);
        return new JsonResponse(2000, "获取成功", page.list);
    }

    @PutMapping("/ban")
    @TokenChecker("admin")
    public JsonResponse ban(@RequestBody User user) {
        boolean val = userService.ban(user.getId(),user.getBanned());
        String ban;
        if(user.getBanned() == 1) {
            ban = "ban";
        } else {
            ban = "unban";
        }
        if(val) {
            return new JsonResponse(2000,ban + " successfully");
        }
        return new JsonResponse(2001,"fail to " + ban);
    }
    @GetMapping("/collect/amount")
    @TokenChecker({"admin", "user"})
    public JsonResponse collectAmount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        int val = userService.getAmount(userId);
        return new JsonResponse(2000, "get the amount successfully", val);
    }

    @PostMapping("/collect/page")
    @TokenChecker({"admin", "user"})
    public JsonResponse collectPage(@RequestBody Page<Blog> page, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = userService.getIdFromToken(token);
        page.init();
        userService.getCollectPage(page, userId);
        return new JsonResponse(2000, "get the page successfully", page.list);
    }
}
