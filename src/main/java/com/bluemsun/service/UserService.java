package com.bluemsun.service;

import com.bluemsun.dao.BlogDao;
import com.bluemsun.dao.BlogUserCollectDao;
import com.bluemsun.dao.UserDao;
import com.bluemsun.entity.Blog;
import com.bluemsun.entity.BlogUserCollect;
import com.bluemsun.entity.Page;
import com.bluemsun.entity.User;
import com.bluemsun.util.IPasswordChecker;
import com.bluemsun.util.JWTUtil;
import com.bluemsun.util.MD5Util;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService
{
    @Resource
    UserDao userDao;
    @Resource
    BlogDao blogDao;
    @Resource
    BlogUserCollectDao blogUserCollectDao;
    @Resource
    JWTUtil jwtUtil;

    public Long getIdFromUuid(Long uuid) {
        return userDao.getIdByUuid(uuid);
    }

    public Long getIdFromToken(String token) {
        return getIdFromUuid(jwtUtil.getUuid(token));
    }

    public Long registerUser(String name, String pwd, String phone, Integer admin) {
        String encryPwd = MD5Util.encryMD5(pwd);
        User user = new User(phone, name, encryPwd);
        user.setAdmin(admin);
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

    public String loginUser(Long uuid, String pwd, Integer admin) {
        String encryPwd = MD5Util.encryMD5(pwd);
        User user = userDao.getUserByUUIDAndPassword(uuid, encryPwd, admin);
        if (user == null) {
            return null;
        }
        String token = null;
        if (admin == 0) {
            token = jwtUtil.createToken(uuid, "user");
        } else {
            token = jwtUtil.createToken(uuid, "admin");
        }
        return token;
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

    public Integer updatePwd(String token, String oldPwd, String newPwd) {
        Long userId = getIdFromToken(token);
        String encryOldPwd = MD5Util.encryMD5(oldPwd);
        User user = userDao.getUserById(userId);
        if (!user.getPwd().equals(encryOldPwd)) {
            return 1;
        }
        if (IPasswordChecker.isEmpty(newPwd)) {
            return 2;
        }
        if (IPasswordChecker.isSpace(newPwd)) {
            return 3;
        }
        if (IPasswordChecker.isTooLong(newPwd)) {
            return 4;
        }
        if (!IPasswordChecker.isComplex(newPwd)) {
            return 5;
        }
        String encryNewPwd = MD5Util.encryMD5(newPwd);
        userDao.updatePwd(encryNewPwd, userId);
        return 0;
    }

    public Integer updateForgetPwd(String newPwd,Long userId) {
        if (IPasswordChecker.isEmpty(newPwd)) {
            return 2;
        }
        if (IPasswordChecker.isSpace(newPwd)) {
            return 3;
        }
        if (IPasswordChecker.isTooLong(newPwd)) {
            return 4;
        }
        if (!IPasswordChecker.isComplex(newPwd)) {
            return 5;
        }
        String encryNewPwd = MD5Util.encryMD5(newPwd);
        userDao.updatePwd(encryNewPwd,userId);
        return 0;
    }

    public int getAmount() {
        return userDao.getAmount();
    }

    public void getPage(Page<User> page) {
        page.list = userDao.getUsersInPage(page.getStartIndex(), page.getPageSize());
    }

    public Boolean ban(Long userId,Integer banned) {
        User user = getInfo(userId,false);
        if(user.getAdmin() == 1) {
            return false;
        }
        userDao.ban(userId,banned);
        return true;
    }

    public void getCollectPage(Page<Blog> page, Long userId) {
        List<Blog> blogs = blogUserCollectDao.getBlogUserCollectsInPage(page.getStartIndex(), page.getPageSize(), userId);
        List<Long> blogIds = new ArrayList<>();
        for (Blog blog : blogs) {
            blogIds.add(blog.getId());
        }
        page.list = blogDao.getBlogs(blogIds, Blog.AUDITED, null);
    }

    public Integer getAmount(Long userId) {
        return blogUserCollectDao.getBlogUserCollectAmount(userId);
    }
}
