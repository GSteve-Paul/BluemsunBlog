package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
public class Blog implements Serializable
{
    public static final Integer AUDITED = 1;
    public static final Integer NOTAUDITED = 0;
    public static final Integer UP = 1;
    public static final Integer NOTUP = 0;
    String title;
    String content;
    String introduction;
    Long likes;
    Integer audit;
    Integer up;
    Long userId;
    Timestamp createTime;
    Timestamp updateTime;
    Long id;

    public Blog(String content, Long userId) {
        this.content = content;
        this.userId = userId;
    }

    public Blog() {}
}
