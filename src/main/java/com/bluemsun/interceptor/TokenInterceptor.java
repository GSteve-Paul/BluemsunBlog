package com.bluemsun.interceptor;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.util.JWTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;

public class TokenInterceptor implements HandlerInterceptor
{
    @Resource
    JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            TokenChecker annotation = handlerMethod.getMethod().getAnnotation(TokenChecker.class);
            if (null != annotation) {
                InputStream inputStream = request.getInputStream();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(inputStream);
                String token = jsonNode.get("token").asText();
                boolean isok = false;
                for (String type : annotation.value()) {
                    isok = isok || jwtUtil.checkToken(token, type);
                }
                if (isok) {
                    return true;
                } else {
                    JsonResponse jsonResponse = new JsonResponse(2500, "not authorized", null);
                    PrintWriter printWriter = response.getWriter();
                    printWriter.write(objectMapper.writeValueAsString(jsonResponse));
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
