package com.bluemsun.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

public class UpdateMySQLJob implements Job
{
    @Resource
    BlogUserLikeService blogUserLikeService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //blogUserLikeService.saveInformationFromRedisToMySQL();
    }
}
