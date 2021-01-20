package com.mind.links.cache.redisson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * date: 2021-01-16 08:10
 * description
 *
 * @author qiDing
 */
@SpringBootApplication
@EnableWebFlux
public class RedissonApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedissonApplication.class, args);
    }
}
