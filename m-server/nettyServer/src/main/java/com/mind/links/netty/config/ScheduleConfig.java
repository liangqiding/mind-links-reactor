package com.mind.links.netty.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Date: 2020/6/06 0010 13:55
 * Description: BoundedElastic线程池配置
 *
 * @author qiding
 */
@Configuration
@ApiModel("自定义有边界的BoundedElastic线程池")
public class ScheduleConfig {

    @ApiModelProperty("系统线程数")
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    @ApiModelProperty("线程池线程数（推荐配置：系统线程数*N,这个N根据实际情况配置,cpu单核处理能力强的,N可配置大于4,我这里因为启动很多服务,所有配置保守些）")
    private static final Integer THREAD_CAP = CPU_NUM;

    @ApiModelProperty("最大排队任务数")
    private final static Integer QUEUED_TASK_CAP = 1000;

    @ApiModelProperty("线程名")
    private final static String NAME = "nettyService-";


    @Bean
    public Scheduler myScheduler() {
        return Schedulers.newBoundedElastic(THREAD_CAP, QUEUED_TASK_CAP, NAME);
    }

}
