package com.mind.links.security.security;

import com.alibaba.fastjson.JSONObject;
import com.mind.links.common.response.ResponseResult;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


/**
 * @author qiDing
 * date: 2021-01-04 16:51
 * @version v1.0.0
 * description
 */

public class JsonServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        String body = new ResponseResult<>("666").toJsonString();
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
