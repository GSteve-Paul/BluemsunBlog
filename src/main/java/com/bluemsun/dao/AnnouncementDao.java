package com.bluemsun.dao;

import com.bluemsun.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnouncementDao
{
    Integer insertAnnouncement(Announcement announcement);

    List<Announcement> getAnnouncementPage(int start,int len);
    Long getAnnounceAmount();
}
