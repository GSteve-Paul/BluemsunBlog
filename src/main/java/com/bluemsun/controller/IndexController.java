package com.bluemsun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController
{
    @GetMapping("/index")
    public String getIndex() {
        return "This is the index of the website.";
    }
}
