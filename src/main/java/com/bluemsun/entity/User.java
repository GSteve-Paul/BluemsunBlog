package com.bluemsun.entity;

import com.bluemsun.util.MyUUIDUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class User implements Serializable
{
    String name;
    String pwd;
    String photo;
    Long phone;
    Integer admin;
    Integer banned;
    Long uuid;
    Timestamp createTime;
    Timestamp updateTime;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    public User() {
    }

    public User(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    //only for register
    public User(Long phone, String name, String pwd) {
        this.phone = phone;
        this.name = name;
        this.pwd = pwd;
        generateUuid();
    }

    public User(Long uuid, String pwd, Long id) {
        this.uuid = uuid;
        this.pwd = pwd;
        this.id = id;
    }

    public Long generateUuid() {
        return this.uuid = MyUUIDUtil.get10bitUUID();
    }
}
