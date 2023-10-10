package com.bluemsun.dao;

import com.bluemsun.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao
{
    User getUser(User user);

    default User getUserById(Integer id) {
        User user = new User(null, null, id);
        return getUser(user);
    }

    default User getUserByUsernameAndPassword(String username, String password) {
        User user = new User(username, password, null);
        return getUser(user);
    }

    User isExist(String username);

    int insertUser(User user);

    int deleteUser(int id);

    int getAmount();

    List<User> getUsersInPage(int start, int len);

    int updateUsername(String username, int id);

    int updatePassword(String password, int id);
}
