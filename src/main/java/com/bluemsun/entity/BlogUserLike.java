package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class BlogUserLike implements Serializable
{
    @Id
    Long id;
    Long userId;
    Long blogId;
    Integer state;

    public BlogUserLike() {}

    public BlogUserLike(Long id, Long userId, Long blogId, Integer state) {
        this.id = id;
        this.userId = userId;
        this.blogId = blogId;
        this.state = state;
    }

    public BlogUserLike(Long userId, Long blogId) {
        this.userId = userId;
        this.blogId = blogId;
    }

    public BlogUserLike(Long userId, Long blogId, Integer state) {
        this.userId = userId;
        this.blogId = blogId;
        this.state = state;
    }
}
