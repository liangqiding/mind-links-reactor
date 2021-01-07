package com.mind.links.security.security.manage;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 将表单里的参数转为AuthenticationToken对象
 * <p>
 * date: 2021-01-07 09:02
 *
 * @author qiDing
 */
@Component
public class ManageAuthenticationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        HttpHeaders headers = serverWebExchange.getRequest().getHeaders();
        String manageToken = headers.getFirst("MANAGE_TOKEN");
        return webFilterChain.filter(serverWebExchange);
    }

}

