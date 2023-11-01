package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Column implements Serializable
{
    String name;
    Timestamp createTime;
    Timestamp updateTime;
    @Id
    Long id;

    public Column() {}

    public Column(String name) {
        this.name = name;
    }

    public Column(String name, Long id) {
        this.name = name;
        this.id = id;
    }
}
