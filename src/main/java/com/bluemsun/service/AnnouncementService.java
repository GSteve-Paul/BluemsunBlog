package com.bluemsun.service;

import com.bluemsun.controller.AnnouncementServer;
import com.bluemsun.dao.AnnouncementDao;
import com.bluemsun.entity.Announcement;
import com.bluemsun.entity.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class AnnouncementService
{
    @Resource
    AnnouncementDao announcementDao;
    @Resource
    AnnouncementServer announcementServer;
    @Resource
    UserService userService;

    @Transactional
    public void announce(String message,String token) {
        Long userId = userService.getIdFromToken(token);
        Announcement announcement = new Announcement(message,userId);
        announcementServer.announce(message);
        announcementDao.insertAnnouncement(announcement);
    }

    public Long getAmount() {
        return announcementDao.getAnnounceAmount();
    }

    public void getPage(Page<Announcement> page) {
        page.list = announcementDao.getAnnouncementPage(page.getStartIndex(),page.getPageSize());
    }
}
