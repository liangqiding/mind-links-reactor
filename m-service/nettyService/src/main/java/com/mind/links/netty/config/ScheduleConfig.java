package com.mind.links.netty.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author : qiDing
 * date: 2021-01-02 13:20
 * @version v1.0.0
 */
@Configuration
@ApiModel("reactor线程池配置")
public class ScheduleConfig {

    @ApiModelProperty("线程数")
    private final static Integer THREAD_CAP = 10;

    @ApiModelProperty("最大排队任务数")
    private final static Integer QUEUED_TASK_CAP = 1000;

    @ApiModelProperty("线程名")
    private final static String NAME = "nettyService-";

    /**
     * 创建reactor线程池
     */
    @Bean
    public Scheduler myScheduler() {
        return Schedulers.newBoundedElastic(THREAD_CAP, QUEUED_TASK_CAP, NAME);
    }

}
