package com.mind.links.security.security.handler;

import com.mind.links.common.enums.LinksExceptionEnum;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.security.config.LinksAuthException;
import lombok.SneakyThrows;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

/**
 * date: 2021-01-09 13:21
 * description 普通异常处理
 *
 * @author qiDing
 */
@Component
@Order(-2)
public class ExceptionHandler implements ErrorWebExceptionHandler {

    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        return serverWebExchange
                .getResponse()
                .writeWith(Mono.just(throwable)
                        .map(t -> response.bufferFactory()
                                .wrap(new ResponseResult<Object>(LinksExceptionEnum.BIND_EXCEPTION.getCode(),t.getMessage()).toJsonString().getBytes(StandardCharsets.UTF_8)))
                );
    }
}

