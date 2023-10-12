package com.bluemsun.controller;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.entity.User;
import com.bluemsun.interceptor.LoginChecker;
import com.bluemsun.interceptor.RegisterChecker;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController
{
    @Resource
    UserService userService;

    @PostMapping("/register")
    @RegisterChecker
    public JsonResponse register(@RequestBody User user) {
        JsonResponse jsonResponse = new JsonResponse();
        Long uuid = userService.register(user.getName(), user.getPwd(), user.getPhone());
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
        String token = userService.login(user.getUuid(), user.getPwd());

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
    @TokenChecker({"user"})
    public JsonResponse getInfo(@RequestBody String token) {
        Long userId = userService.getIdFromToken(token);
        User user = userService.getInfo(userId, false);
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setCode(2000);
        jsonResponse.setMessage("Get info successfully");
        jsonResponse.setData(user);
        return jsonResponse;
    }

    @PutMapping("/info")
    @RegisterChecker
    @TokenChecker("user")
    public JsonResponse putInfo(@RequestBody String token, @RequestBody User user) {
        Long userId = userService.getIdFromToken(token);
        user.setId(userId);
        int val = userService.updateInfo(user);
        JsonResponse jsonResponse = new JsonResponse(2000, "update successfully", null);
        if (val == 0) {
            jsonResponse = new JsonResponse(2001, "unknown error", null);
        }
        return jsonResponse;
    }

    @GetMapping("/amount")
    @TokenChecker({"user", "admin"})
    public JsonResponse getAmount() {
        int amount = userService.getAmount();
        return new JsonResponse(2000, "获取成功", amount);
    }

    @GetMapping("/page")
    @TokenChecker({"user", "admin"})
    public JsonResponse getPage(@RequestBody Page<User> page) {
        page.init();
        userService.getPage(page);
        return new JsonResponse(2000, "获取成功", page.list);
    }

}
