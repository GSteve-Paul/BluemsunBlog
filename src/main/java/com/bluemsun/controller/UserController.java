package com.bluemsun.controller;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.entity.User;
import com.bluemsun.interceptor.LoginChecker;
import com.bluemsun.interceptor.RegisterChecker;
import com.bluemsun.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController
{
    private int getUserId(HttpSession session) {
        return (int) session.getAttribute("userId");
    }

    @Resource
    UserService userService;

    @GetMapping("/status")
    public JsonResponse status(HttpSession session) {
        JsonResponse jsonResponse = new JsonResponse();
        try {
            int userId = (int) session.getAttribute("userId");
            jsonResponse.setData(userId);
            jsonResponse.setCode(2000);
            jsonResponse.setMessage("Success");
        } catch (NullPointerException e) {
            jsonResponse.setData(null);
            jsonResponse.setCode(2001);
            jsonResponse.setMessage("No login");
        }
        return jsonResponse;
    }

    @PostMapping("/register")
    @RegisterChecker
    public JsonResponse register(@RequestBody User user) {
        JsonResponse jsonResponse = new JsonResponse();
        int val = userService.register(user);
        jsonResponse.setCode(2000 + val);
        switch (val) {
            case 0: jsonResponse.setMessage("Register successfully");
                break;
            case 1: jsonResponse.setMessage("Unknown error");
                break;
            case 2: jsonResponse.setMessage("The username already exists");
                break;
        }
        return jsonResponse;
    }

    @PostMapping("/login")
    @LoginChecker
    public JsonResponse login(@RequestBody User user) {
        JsonResponse jsonResponse = new JsonResponse();
        String token = userService.login(user);

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
    public JsonResponse logout(@RequestBody String token) {
        userService.logout(token);
        return new JsonResponse(2000, "Logout successfully");
    }

    @GetMapping("/info")
    public JsonResponse getInfo(HttpSession session) {
        int userId = getUserId(session);
        User user = userService.info(userId);
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setCode(2000);
        jsonResponse.setMessage("Get info successfully");
        jsonResponse.setData(user);
        return jsonResponse;
    }

    @GetMapping("/amount")
    public JsonResponse getAmount() {
        int amount = userService.getAmount();
        return new JsonResponse(2000, "获取成功", amount);
    }

    @GetMapping("/page")
    public JsonResponse getPage(@RequestBody Page<User> page) {
        page.init();
        userService.getPage(page);
        return new JsonResponse(2000, "获取成功", page.list);
    }

    @PutMapping("/info")
    @RegisterChecker
    public JsonResponse updateInfo(@RequestBody User user, HttpSession session) {
        int userId = getUserId(session);
        Integer res = userService.updateInfo(user, userId);
        if (0 == res) {
            return new JsonResponse(2000, "修改成功");
        } else {
            return new JsonResponse(2001, "修改失败");
        }
    }
}
