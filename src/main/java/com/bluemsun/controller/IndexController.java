package com.bluemsun.controller;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.util.JWTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jdk.internal.util.xml.impl.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController
{
    @Resource
    JWTUtil jwtUtil;

    @GetMapping("/index")
    public String getIndex() {
        return "This is the index of the website.";
    }

    @GetMapping("/state")
    public JsonResponse getState(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims body = jwtUtil.checkToken(token);
        if(body == null) {
            return new JsonResponse(2500,"not authorized",null);
        }
        Long uuid = jwtUtil.getUuid(body);
        String type = jwtUtil.getType(body);
        Map<String,Object> mp = new HashMap<>();
        mp.put("uuid",uuid);
        mp.put("type",type);
        return new JsonResponse(2000,"authorized",mp);
    }
}
