package com.mind.links.security.security.handler;


import com.mind.links.common.enums.LinksExceptionEnum;
import com.mind.links.common.response.ResponseResult;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

/**
 * date: 2021-01-09 09:37
 * description  未授权统一返回，不设置会返回 httpStatus=401 的 null 回复
 *
 * @author qiDing
 */
@Component
public class UnauthorizedAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        return exchange
                .getResponse()
                .writeWith(Mono.just("未授权")
                        .map(s -> response.bufferFactory()
                                .wrap(new ResponseResult<String>(LinksExceptionEnum.UNAUTHORIZED.getCode()).toJsonString().getBytes(StandardCharsets.UTF_8)))
                );
    }
}
