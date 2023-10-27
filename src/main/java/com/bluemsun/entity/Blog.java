package com.bluemsun.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
public class Blog implements Serializable
{
    String title;
    String content;
    String introduction;
    Long likes;
    Boolean audit;
    Boolean up;
    Long userId;
    Timestamp createTime;
    Timestamp updateTime;
    Long id;

    public static final Boolean AUDITED = true;
    public static final Boolean NOTAUDITED = false;

    public static final Boolean UP = true;
    public static final Boolean NOTUP = false;

    public Blog(String content, Long userId) {
        this.content = content;
        this.userId = userId;
    }

    public Blog() {}
}
