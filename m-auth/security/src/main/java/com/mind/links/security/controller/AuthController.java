package com.mind.links.security.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.security.security.handler.AuthHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import javax.validation.constraints.NotBlank;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
@RestController
@Slf4j
@RequestMapping("/auth")
@Api("用户认证")
@AllArgsConstructor
@Validated
public class AuthController {

    private final AuthHandler authHandler;

    @PostMapping(value = "/login")
    @ApiOperation("用户登录")
    public Mono<ResponseResult<String>> login(@NotBlank(message = "账号不能为空") @RequestPart(value = "username") String username,
                                              @NotBlank(message = "密码不能为空") @RequestPart(value = "password") String password,
                                              final ServerHttpRequest request) {
        return ResponseResult.transform(authHandler.loginAuth(username, password,request.getHeaders().getFirst("Authorization")));
    }
}

