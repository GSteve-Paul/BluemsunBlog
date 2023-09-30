package com.bluemsun.config;

import com.bluemsun.interceptor.LoginInterceptor;
import com.bluemsun.interceptor.RegisterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new RegisterInterceptor()).addPathPatterns("/**");
    }
}
