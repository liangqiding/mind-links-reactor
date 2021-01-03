package com.mind.links.redisson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-10 11:38
 * @version v1.0.0
 */
@SpringBootApplication
@EnableWebFlux
public class RedissonApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedissonApplication.class, args);
    }
}
