package com.mind.links.security.security;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * date: 2021-01-07 10:51
 * description
 *
 * @author qiDing
 */
public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {


    /**
     * 将此功能应用于当前的WebExchange，完成后将返回Authentication对象*
     *
     * @param serverWebExchange
     * @return
     */
    @Override
    public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(ServerHttpBearerAuthenticationConverter::create).log();
    }

    public static Mono<Authentication> create(ServerWebExchange serverWebExchange) {
        MultiValueMap<String, String> queryParams = serverWebExchange.getRequest().getQueryParams();
        String username = queryParams.getFirst("username");
        String password = queryParams.getFirst("password");
        Authentication authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        System.out.println("ServerHttpBearerAuthenticationConverter----" + JSON.toJSONString(authRequest));
        return Mono.justOrEmpty(authRequest);
    }
}
