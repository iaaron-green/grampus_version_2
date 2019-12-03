package com.app.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SchedulerExample {
    //fixedRate in milliseconds
    @Scheduled(fixedRate = 3000)
    public void reportCurrentData() {
        System.out.println("Scheduler working: " + new Date());
    }
}
