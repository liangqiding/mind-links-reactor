package com.mind.links.security.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.mind.links.common.response.ResponseResult;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * date: 2021-01-07 09:00
 * description
 *
 * @author qiDing
 */
@Component
public class FailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        ResponseResult<String> result = new ResponseResult<>();
        if (exception instanceof UsernameNotFoundException) {
            // 用户不存在
            result.setCode(46001);
            result.setMessage(exception.getMessage());
        } else if (exception instanceof BadCredentialsException) {
            // 密码错误
            result.setCode(46002);
            result.setMessage(exception.getMessage());
        } else if (exception instanceof LockedException) {
            // 用户被锁
            result.setCode(46003);
            result.setMessage(exception.getMessage());
        } else {
            // 系统错误
            result.setCode(46004);
            result.setMessage(exception.getMessage());
        }
        String body = result.toJsonString();
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}


