package com.mind.links.security.security;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-07 10:51
 * description
 *
 * @author qiDing
 */
public class MyServerAuthenticationConverter implements ServerAuthenticationConverter {


    /**
     * 将此功能应用于当前的WebExchange，完成后将返回Authentication对象*
     *
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(MyServerAuthenticationConverter::create).log();
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
