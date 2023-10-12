package com.bluemsun.config;

import antlr.Token;
import com.bluemsun.interceptor.LoginInterceptor;
import com.bluemsun.interceptor.RegisterChecker;
import com.bluemsun.interceptor.RegisterInterceptor;
import com.bluemsun.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Bean
    LoginInterceptor loginInterceptor() {return new LoginInterceptor();}
    @Bean
    TokenInterceptor tokenInterceptor() {return new TokenInterceptor();}
    @Bean
    RegisterInterceptor registerInterceptor(){return new RegisterInterceptor();}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(registerInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(tokenInterceptor()).addPathPatterns("/**");
    }
}
