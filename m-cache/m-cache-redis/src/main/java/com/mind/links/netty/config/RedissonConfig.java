package com.mind.links.netty.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * description : TODO Redisson 核心配置
 *
 * @author : qiDing
 * date: 2020-12-10 14:27
 * @version v1.0.0
 */
@Configuration
@ComponentScan
@EnableCaching
public class RedissonConfig {
    @Autowired
    RedisProperties redisProperties;


    /**
     * 配置参考 https://github.com/redisson/redisson/wiki/2.-Configuration
     */
    @Bean
    public RedissonClient getRedisson() throws Exception {
        RedissonClient redisson = null;
        Config config = new Config();
        config.useSingleServer()
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase())
                .setAddress(redisProperties.getAddress())
                .setConnectionPoolSize(redisProperties.getConnectionPoolSize())
                .setTimeout(redisProperties.getTimeout())
        ;
        Codec codec = (Codec) ClassUtils.forName(redisProperties.getCodec(), ClassUtils.getDefaultClassLoader()).newInstance();
        config.setCodec(codec);
        redisson = Redisson.create(config);
        return redisson;
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>(16);
        // 单位ms ttl最长有效时间  maxIdleTime 最大空闲时间
        config.put("user2", new CacheConfig(20 * 60 * 1000, 12 * 60 * 1000));
        config.put("user", new CacheConfig(20 * 60 * 1000, 12 * 60 * 1000));
        return new RedissonSpringCacheManager(redissonClient, config);

    }
}
