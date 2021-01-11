package com.mind.links.security.security.handler;

import com.mind.links.security.jwt.TokenProvider;
import com.mind.links.security.security.manage.ManageAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-09 11:02
 * description 处理登录  jwt 认证
 *
 * @author qiDing
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {

    private final TokenProvider tokenProvider;

    private final ManageAuthenticationManager manageAuthenticationManager;

    public Mono<String> loginAuth(String username, String password, String clientId) {
        log.debug("clientId:" + clientId);
        return manageAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .flatMap(this::setAuthentication)
                .flatMap(auth -> tokenProvider.createToken(auth, clientId));
    }

    public Mono<Authentication> checkToken(String token) {
        return tokenProvider.getAuthentication(token);
    }


    /**
     * 把认证信息添加到security 上下文中，这步对于我们的分布式系统可有可无
     */
    private Mono<Authentication> setAuthentication(Authentication authentication) {
        return Mono.just(ReactiveSecurityContextHolder.withAuthentication(authentication)).map(context -> authentication);
    }
}
