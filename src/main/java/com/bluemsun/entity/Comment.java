package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
public class Comment implements Serializable
{
    @Id
    Long id;
    String content;
    Timestamp createTime;
    Timestamp updateTime;
    Long likes;
    Long commentId;
    Long blogId;
    Long userId;

    public Comment() {
    }

    public Comment(String content, Long userId) {
        this.content = content;
        this.userId = userId;
    }
}
