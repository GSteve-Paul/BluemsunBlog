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
public class Announcement implements Serializable
{
    String content;
    Timestamp createTime;
    Timestamp updateTime;
    Long userId;
    @Id
    Long id;

    public Announcement() {}

    public Announcement(String content, Long userId) {
        this.content = content;
        this.userId = userId;
    }

    public Announcement(String content, Long userId, Long id) {
        this.content = content;
        this.userId = userId;
        this.id = id;
    }
}
