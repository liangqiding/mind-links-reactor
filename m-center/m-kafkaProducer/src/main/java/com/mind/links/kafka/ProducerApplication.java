package com.mind.links.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-23 15:14
 * @version v1.0.0
 */
@SpringBootApplication
@EnableWebFlux
public class ProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
}
