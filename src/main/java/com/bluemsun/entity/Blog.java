package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Blog implements Serializable
{
    public static final Integer AUDITED = 1;
    public static final Integer NOTAUDITED = 0;
    public static final Integer UP = 1;
    public static final Integer NOTUP = 0;
    String title;
    String content;
    String introduction;
    String photo;
    Long likes;
    Integer audit;
    Integer up;
    Long userId;
    Timestamp createTime;
    Timestamp updateTime;
    @Id
    Long id;

    public Blog(String content, Long userId) {
        this.content = content;
        this.userId = userId;
    }

    public Blog() {}
}
