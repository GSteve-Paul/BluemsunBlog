package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class BlogUserCollect implements Serializable
{
    @Id
    Long id;
    Long blogId;
    Long userId;
    Integer state;

    public BlogUserCollect() {
    }

    public BlogUserCollect(Long id, Long blogId, Long userId, Integer state) {
        this.id = id;
        this.blogId = blogId;
        this.userId = userId;
        this.state = state;
    }

    public BlogUserCollect(Long blogId, Long userId) {
        this.blogId = blogId;
        this.userId = userId;
    }

    public BlogUserCollect(Long blogId, Long userId, Integer state) {
        this.blogId = blogId;
        this.userId = userId;
        this.state = state;
    }
}
