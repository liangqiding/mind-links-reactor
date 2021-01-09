package com.mind.links.security.security.handler;

import com.mind.links.common.enums.LinksExceptionEnum;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.security.config.LinksAuthException;
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
 * description Authentication 失败处理程序
 *
 * @author qiDing
 */
@Component
public class FailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        ResponseResult<String> result;
        if (exception instanceof UsernameNotFoundException) {
            result = new ResponseResult<>(LinksExceptionEnum.USER_NOT_FOUND.getCode());
        } else if (exception instanceof BadCredentialsException) {
            result = new ResponseResult<>(LinksExceptionEnum.BAD_CREDENTIALS.getCode());
        } else if (exception instanceof LockedException) {
            result = new ResponseResult<>(LinksExceptionEnum.USER_LOCKED.getCode());
        } else if (exception instanceof LinksAuthException) {
            result = new ResponseResult<>(LinksExceptionEnum.BAD_REQUEST.getCode(), exception.getMessage());
        } else {
            result = new ResponseResult<>(LinksExceptionEnum.OTHER_ERROR.getCode());
        }
        DataBuffer buffer = response.bufferFactory().wrap(result.toJsonString().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}


