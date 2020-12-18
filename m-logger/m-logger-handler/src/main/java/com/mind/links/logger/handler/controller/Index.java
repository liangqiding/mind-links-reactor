package com.mind.links.logger.handler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-17 09:43
 * @version v1.0.0
 */
@RestController
@Slf4j
public class Index {
    @GetMapping("/")
    public String test() {
        log.debug("===debug日记测试===");
        log.info("===info日记测试===");
        log.warn("===warn日记测试===");
        log.error("===error日记测试===");
//        int err=1/0;
        return "success";
    }
}
