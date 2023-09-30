package com.bluemsun.interceptor;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.User;
import com.bluemsun.util.IPasswordChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;


public class LoginInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if(null != handlerMethod.getMethod().getAnnotation(LoginChecker.class)) {
                InputStream inputStream = request.getInputStream();
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(inputStream,User.class);
                String username = user.getUsername();
                String password = user.getPassword();
                JsonResponse jsonResponse = new JsonResponse();
                if (IPasswordChecker.isEmpty(password, username)) {
                    jsonResponse.setCode(2011);
                    jsonResponse.setMessage("Empty string");
                }
                if (IPasswordChecker.isSpace(password, username)) {
                    jsonResponse.setCode(2012);
                    jsonResponse.setMessage("Space exists");
                }
                if(jsonResponse.getCode() == 0) {
                    return true;
                } else {
                    String jsonString = objectMapper.writeValueAsString(jsonResponse);
                    PrintWriter printWriter = response.getWriter();
                    printWriter.write(jsonString);
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
