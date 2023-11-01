package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class CommentUserLike implements Serializable
{
    @Id
    Long id;
    Long commentId;
    Long userId;
    Integer state;

    public CommentUserLike() {
    }

    public CommentUserLike(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public CommentUserLike(Long commentId, Long userId, Integer state) {
        this.commentId = commentId;
        this.userId = userId;
        this.state = state;
    }
}
