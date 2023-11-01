package com.bluemsun.config;

import com.bluemsun.service.CommentService;
import com.bluemsun.service.LikeBlogRedisService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

@Slf4j
public class UpdateMySQLJob implements Job
{
    @Resource
    LikeBlogRedisService likeBlogRedisService;
    @Resource
    CommentService commentService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("start updating MySQL");
        //blog like
        likeBlogRedisService.saveInformationFromRedisToMySQL();
        //comment like
        commentService.saveInformationFromRedisToMySQL();
        log.info("update MySQL finished");
    }
}
