package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class BlogUserLike
{
    @Id
    Long id;
    Long userId;
    Long blogId;
}
