package com.mind.links.security.security;

import com.mind.links.security.config.LinksAuthException;
import com.mind.links.security.jwt.TokenCommon;
import com.mind.links.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;


/**
 * date: 2021-01-07 10:51
 * description 身份认证转换器，在这里进行 jwt校验 返回 Authentication
 *
 * @author qiDing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private final TokenProvider tokenProvider;

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {

        return Mono.justOrEmpty(serverWebExchange)
                .map(ServerWebExchange::getRequest)
                .flatMap(TokenCommon::getToken)
                .filter(token -> !StringUtils.isEmpty(token))
                .switchIfEmpty(LinksAuthException.errors("无效的token"))
                .flatMap(tokenProvider::getAuthentication)
                .filter(Objects::nonNull);
    }

}
