package com.mind.links.security.security.handler;

import com.alibaba.fastjson.JSON;
import com.mind.links.security.jwt.TokenProvider;
import com.mind.links.security.security.manage.ManageAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;


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

    public Mono<String> loginAuth(String username, String password,String clientId) {
        log.debug("clientId:"+clientId);
        return manageAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .map(auth -> {
                    log.debug(JSON.toJSONString(auth));
                    ReactiveSecurityContextHolder.withAuthentication(auth);
                    return tokenProvider.createToken(auth,clientId);
                });
    }

}
