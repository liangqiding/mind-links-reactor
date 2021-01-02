package com.mind.links.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-21 13:40
 * @version v1.0.0
 */
@SpringBootApplication
@EnableWebFlux
public class MinioApplication  {
    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class, args);
    }

}
