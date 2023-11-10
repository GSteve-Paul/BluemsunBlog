package com.bluemsun.controller;

import com.bluemsun.entity.Announcement;
import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/announce")
public class AnnouncementController
{
    @Resource
    AnnouncementService announcementService;
    @PostMapping()
    @TokenChecker("admin")
    public JsonResponse sendAnnounce(@RequestBody Announcement announcement, HttpServletRequest request) {
        String token = request.getHeader("token");
        announcementService.announce(announcement.getContent(),token);
        return new JsonResponse(2000,"send announce successfully");
    }

    @GetMapping("/page")
    public JsonResponse getAmount() {
        Long val = announcementService.getAmount();
        JsonResponse jsonResponse = new JsonResponse(2000,"get amount successfully",val);
        return jsonResponse;
    }

    @PostMapping("/page")
    public JsonResponse getPage(@RequestBody Page<Announcement> page) {
        page.init();
        announcementService.getPage(page);
        JsonResponse jsonResponse = new JsonResponse(2000,"get page " + page.getCurrentPage() + " successfully",page.list);
        return jsonResponse;
    }
}
