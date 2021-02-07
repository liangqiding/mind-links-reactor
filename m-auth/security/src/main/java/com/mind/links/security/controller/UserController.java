package com.mind.links.security.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.security.domain.vo.LinksUserVo;
import com.mind.links.security.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * date: 2021-02-07 13:43
 * description
 *
 * @author qiDing
 */
@RestController
@Slf4j
@RequestMapping("/user")
@Api("用户信息")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping(value = "/info/get")
    @ApiOperation("获取用户信息")
    public Mono<ResponseResult<LinksUserVo>> getUserInfo(@RequestHeader(value = "userId") Long userId) {
        return ResponseResult.transform(userService.getUserById(userId));
    }
}
