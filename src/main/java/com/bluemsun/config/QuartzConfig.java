package com.bluemsun.config;

import com.bluemsun.service.UpdateMySQLJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig
{
    @Bean
    public JobDetail updateMySQLDetail() {
        return JobBuilder.newJob(UpdateMySQLJob.class)
                .withIdentity("updateMySQLJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger updateMySQLTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(1)
                .repeatForever();
        return TriggerBuilder.newTrigger()
                .forJob(updateMySQLDetail())
                .withIdentity("updateMySQLTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
