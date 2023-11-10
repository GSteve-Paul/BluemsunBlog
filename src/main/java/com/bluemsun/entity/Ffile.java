package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class Ffile implements Serializable
{
    public String name;
    public Long blogId;
    @Id
    public Long id;

    public Ffile() {}

    public Ffile(String name, Long blogId, Long id) {
        this.name = name;
        this.blogId = blogId;
        this.id = id;
    }

    public Ffile(String name, Long blogId) {
        this.name = name;
        this.blogId = blogId;
    }
}
