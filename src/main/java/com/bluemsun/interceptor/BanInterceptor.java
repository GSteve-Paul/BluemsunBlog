package com.bluemsun.interceptor;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.User;
import com.bluemsun.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;

public class BanInterceptor implements HandlerInterceptor
{
    @Resource
    UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if(null != handlerMethod.getMethod().getAnnotation(BanChecker.class)) {
                String token = request.getHeader("token");
                Long userId = userService.getIdFromToken(token);
                User user = userService.getInfo(userId,false);
                if(user.getBanned() == 1) {
                    JsonResponse jsonResponse = new JsonResponse(2600, "you are now banned", null);
                    ObjectMapper objectMapper = new ObjectMapper();
                    PrintWriter printWriter = response.getWriter();
                    printWriter.write(objectMapper.writeValueAsString(jsonResponse));
                    return false;
                }
                return true;
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
