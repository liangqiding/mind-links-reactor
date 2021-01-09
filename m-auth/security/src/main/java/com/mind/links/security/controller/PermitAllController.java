package com.mind.links.security.controller;

import com.mind.links.common.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-09 10:53
 * description
 *
 * @author qiDing
 */
@RestController
public class PermitAllController {

    @GetMapping("/api/get")
    public Mono<ResponseResult<String>> getUserInfo() {
        return Mono.just(new ResponseResult<>());
    }

}
