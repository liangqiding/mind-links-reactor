package com.mind.links.minio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author qiDing
 */
@Configuration
public class ScheduleConfig {

    /**
     * 创建reactor线程池
     */
    @Bean
    public Scheduler myScheduler() {
        return Schedulers.newBoundedElastic(10, 10000, "minio-");
    }

}
