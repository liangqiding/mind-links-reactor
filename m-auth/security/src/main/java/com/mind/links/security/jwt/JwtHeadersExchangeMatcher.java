package com.mind.links.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * date: 2021-01-05 09:50  自定义认证配置器
 *
 * @author qiding
 */
@Component
@Slf4j
public class JwtHeadersExchangeMatcher implements ServerWebExchangeMatcher {

    /**
     * 对带 Authorization 请求头且包含Bearer的请求进行token效验
     */
    @Override
    public Mono<MatchResult> matches(final ServerWebExchange exchange) {
        return Mono.just(exchange)
                .map(ServerWebExchange::getRequest)
                .map(ServerHttpRequest::getHeaders)
                .filter(h -> h.containsKey(HttpHeaders.AUTHORIZATION))
                .filter(h -> TokenCommon.MATCH_BEARER.test(h.getFirst(HttpHeaders.AUTHORIZATION)))
                .flatMap(callback -> MatchResult.match())
                .switchIfEmpty(MatchResult.notMatch());
    }

}
