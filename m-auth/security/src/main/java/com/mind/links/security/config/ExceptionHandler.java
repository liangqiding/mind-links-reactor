package com.mind.links.security.config;

import com.mind.links.common.exception.LinksExceptionHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

/**
 * date: 2021-01-09 13:21
 * description 公共异常处理，负责处理security代理以外的异常
 *
 * @author qiDing
 */
@Component
@Slf4j
public class ExceptionHandler implements ErrorWebExceptionHandler {

    @Value("${exception.printStackTrace:true}")
    private Boolean printStackTrace;

    @SneakyThrows
    @Override
    @Nonnull
    public Mono<Void> handle(ServerWebExchange serverWebExchange, @Nonnull Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        if (printStackTrace){
            throwable.printStackTrace();
        }
        log.error("throwable:"+throwable);
        return serverWebExchange
                .getResponse()
                .writeWith(Mono.just(throwable)
                        .flatMap(LinksExceptionHandler::errorHandler)
                        .map(responseResult -> response.bufferFactory()
                                .wrap(responseResult.toJsonString().getBytes(StandardCharsets.UTF_8))));
    }
}

