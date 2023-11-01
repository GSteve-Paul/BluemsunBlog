package com.bluemsun.dao;

import com.bluemsun.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao
{
    User getUser(User user);

    default User getUserById(Long id) {
        User user = new User(null, null, id);
        return getUser(user);
    }

    default User getUserByUUIDAndPassword(Long uuid, String pwd, Integer admin) {
        User user = new User(uuid, pwd, (Long) null);
        user.setAdmin(admin);
        return getUser(user);
    }

    default Long getIdByUuid(Long uuid) {
        User user = new User(uuid, (String) null, (Long) null);
        user = getUser(user);
        if (user == null) {
            return null;
        } else {
            return user.getId();
        }
    }

    Long insertUser(User user);

    int deleteUser(Long id);

    int updateUser(User user);

    int updatePwd(String pwd, Long userId);

    int getAmount();

    List<User> getUsersInPage(int start, int len);

    Integer ban(Long userId,Integer banned);
}
