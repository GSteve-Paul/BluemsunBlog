package com.bluemsun.service;

import com.bluemsun.dao.UserDao;
import com.bluemsun.entity.Page;
import com.bluemsun.entity.User;
import com.bluemsun.util.IPasswordChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public int getAmount() {
        return userDao.getAmount();
    }

    public void getPage(Page<User> page) {
        page.list = userDao.getUsersInPage(page.getStartIndex(),page.getPageSize());
    }

    @Transactional
    public Integer updateInfo(User user,int userId) {
        String username = user.getUsername();
        String password = user.getPassword();
        int ans = 0;
        int result;
        if(userDao.isExist(username) != null) {
            throw new RuntimeException("该用户名已存在");
        }

        result = userDao.updateUsername(username,userId);
        if(result != 1) {
            throw new RuntimeException("username戳啦");
        }
        result = userDao.updatePassword(password,userId);
        if(result != 1) {
            throw new RuntimeException("password戳啦");
        }
        return ans;
    }
}
