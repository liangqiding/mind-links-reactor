package com.mind.links.redisson.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description : redisson配置
 * @author : qiDing
 * date: 2020-12-14 09:27
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ConfigurationProperties(prefix = "redisson.single")
@Component
@ApiModel("redisson配置")
public class RedissonProperties {

    @ApiModelProperty("连接空闲超时，单位：毫秒")
    private Integer idleConnectionTimeout;

    @ApiModelProperty("连接超时，单位：毫秒")
    private Integer connectTimeout;

    @ApiModelProperty("命令等待超时，单位：毫秒")
    private Integer timeout;

    @ApiModelProperty("命令失败重试次数")
    private Integer retryAttempts;

    @ApiModelProperty("命令重试发送时间间隔，单位：毫秒")
    private Integer retryInterval;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("单个连接最大订阅数量")
    private Integer subscriptionsPerConnection;

    @ApiModelProperty("客户端名称")
    private String clientName;

    @ApiModelProperty("服务器地址")
    private String address;

    @ApiModelProperty("从节点发布和订阅连接的最小空闲连接数")
    private Integer subscriptionConnectionMinimumIdleSize;

    @ApiModelProperty("从节点发布和订阅连接池大小")
    private Integer subscriptionConnectionPoolSize;

    @ApiModelProperty("从节点最小空闲连接数")
    private Integer connectionMinimumIdleSize;

    @ApiModelProperty("连接池大小")
    private Integer connectionPoolSize;

    @ApiModelProperty("数据库序号")
    private Integer database;

    @ApiModelProperty("DNS监测时间间隔，单位：毫秒")
    private Integer dnsMonitoringInterval;

    @ApiModelProperty("线程池数量")
    private Integer threads;

    @ApiModelProperty("Netty线程池数量")
    private Integer nettyThreads;

    @ApiModelProperty("序列化方式")
    private String codec;

    @ApiModelProperty("传输模式")
    private String transportMode;

}
