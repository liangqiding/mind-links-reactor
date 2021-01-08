package com.mind.links.security.jwt;

import com.mind.links.security.config.LinksAuthException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-05 09:50
 *
 * @author qidingliang
 */
public class JwtHeadersExchangeMatcher implements ServerWebExchangeMatcher {

    @Override
    public Mono<MatchResult> matches(final ServerWebExchange exchange) {
        return Mono.just(exchange)
                .map(ServerWebExchange::getRequest)
                .map(ServerHttpRequest::getHeaders)
                .filter(h -> h.containsKey(HttpHeaders.AUTHORIZATION))
                .flatMap(callback -> MatchResult.match())
                .switchIfEmpty(LinksAuthException.errors("无效的请求头"));
    }

}
