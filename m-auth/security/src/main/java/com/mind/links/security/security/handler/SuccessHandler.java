package com.mind.links.security.security.handler;


import com.alibaba.fastjson.JSON;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.security.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;


/**
 * date: 2021-01-07 08:48
 * description 验证成功处理程序
 *
 * @author qiDing
 */
@Component
@Slf4j
public class SuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    TokenProvider tokenProvider;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,final Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        String token = tokenProvider.createToken(authentication);
        log.debug("SuccessHandler-token="+token+"::"+authentication);
        ResponseResult<String> result = new ResponseResult<>(token);
        Authentication auth = tokenProvider.getAuthentication(token);
        log.debug("token-解码="+ JSON.toJSONString(auth));
        DataBuffer buffer = response.bufferFactory().wrap(result.toJsonString().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

