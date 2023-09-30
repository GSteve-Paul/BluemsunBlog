package com.bluemsun.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User implements Serializable
{
    String username, password;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Integer id) {
        this.username = username;
        this.password = password;
        this.id = id;
    }
}
