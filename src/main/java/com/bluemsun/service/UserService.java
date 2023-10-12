package com.bluemsun.service;

import com.bluemsun.dao.UserDao;
import com.bluemsun.entity.Page;
import com.bluemsun.entity.User;
import com.bluemsun.util.JWTUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService
{
    @Resource
    UserDao userDao;
    @Resource
    JWTUtil jwtUtil;

    public Long getIdFromUuid(Long uuid) {
        return userDao.getIdByUuid(uuid);
    }

    public Long getIdFromToken(String token) {
        return getIdFromUuid(jwtUtil.getUuid(token));
    }

    public Long register(String name, String pwd, Long phone) {
        User user = new User(phone, name, pwd);
        Long uuid = user.getUuid();
        if (userDao.getIdByUuid(uuid) != null) {
            return 0L;
        }
        Long id = userDao.insertUser(user);
        if (id == 0) {
            return 0L;
        } else {
            return uuid;
        }
    }

    public String login(Long uuid, String pwd) {
        User user = userDao.getUserByUUIDAndPassword(uuid, pwd);
        if (user == null) {
            return null;
        }
        return jwtUtil.createToken(uuid, "user");
    }

    public void logout(String token) {
        jwtUtil.deleteToken(token);
    }

    public User getInfo(Long userId, Boolean keepPwd) {
        User user = userDao.getUserById(userId);
        if (!keepPwd) {
            user.setPwd(null);
        }
        return user;
    }

    public int updateInfo(User user) {
        return userDao.updateUser(user);
    }

    public int getAmount() {
        return userDao.getAmount();
    }

    public void getPage(Page<User> page) {
        page.list = userDao.getUsersInPage(page.getStartIndex(), page.getPageSize());
    }
}
