package com.bluemsun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(value = {"com.bluemsun.filter"})
public class MySpringBootApplication
{

    public static void main(String[] args) {
        SpringApplication.run(MySpringBootApplication.class, args);
    }

}
