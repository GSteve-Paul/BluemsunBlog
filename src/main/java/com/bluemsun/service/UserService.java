package com.bluemsun.service;

import com.bluemsun.dao.UserDao;
import com.bluemsun.entity.User;
import com.bluemsun.util.IPasswordChecker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService
{
    @Resource
    UserDao userDao;

    public int register(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (userDao.isExist(username) != null) {
            return 2;
        }
        user = new User(username, password);
        if (userDao.insertUser(user) == 1) {
            return 0;
        } else {
            return 1;
        }
    }

    public User login(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        user = userDao.getUserByUsernameAndPassword(username, password);
        return user;
    }

    public User info(int userId) {
        User user =  userDao.getUserById(userId);
        return user;
    }
}