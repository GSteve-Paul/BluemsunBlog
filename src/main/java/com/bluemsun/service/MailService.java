package com.bluemsun.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service

public class MailService
{
    @Resource
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    String from;


    private void sendEmail(String content,String subject,String to) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        javaMailSender.send(simpleMailMessage);
    }
    @Async
    public void sendVerificationCode(String to,String code) {
        String content = "验证码是 " + code;
        String subject = "MyBlog";
        sendEmail(content,subject,to);
    }
}
