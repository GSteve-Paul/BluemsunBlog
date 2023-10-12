package com.bluemsun.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TokenChecker
{
    //satisfy one of these types of users
    String[] value();
}
