package com.mind.links.netty.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-14 09:27
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ConfigurationProperties(prefix = "redisson.single")
@Component
public class RedisProperties {

    private Integer idleConnectionTimeout;
    private Integer connectTimeout;
    private Integer timeout;
    private Integer retryAttempts;
    private Integer retryInterval;
    private String password;
    private Integer subscriptionsPerConnection;
    private String clientName;
    private String address;
    private Integer subscriptionConnectionMinimumIdleSize;
    private Integer subscriptionConnectionPoolSize;
    private Integer connectionMinimumIdleSize;
    private Integer connectionPoolSize;
    private Integer database;
    private Integer dnsMonitoringInterval;
    private Integer threads;
    private Integer nettyThreads;
    private String codec;
    private String transportMode;
}
