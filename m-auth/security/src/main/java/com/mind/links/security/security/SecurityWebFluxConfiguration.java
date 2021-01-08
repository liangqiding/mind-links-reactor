package com.mind.links.security.security;


import com.mind.links.security.jwt.JwtHeadersExchangeMatcher;
import com.mind.links.security.security.handler.FailureHandler;
import com.mind.links.security.security.handler.SuccessHandler;
import com.mind.links.security.security.manage.ManageAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import javax.annotation.Resource;


/**
 * @author qiDing
 * 2020/11/7
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityWebFluxConfiguration {

    @Resource
    private SuccessHandler successHandler;
    @Resource
    private FailureHandler failureHandler;
    @Resource
    private ManageAuthenticationManager manageAuthenticationManager;

    /**
     * 白名单
     */
    private static final String[] AUTH_WHITELIST = new String[]{"/manage/login", "/user/login"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain userSecurityFilterChain(ServerHttpSecurity http) {
        http
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .securityMatcher(ServerWebExchangeMatchers.anyExchange())
                .formLogin(n -> n
                        .loginPage("/user/login")
                        .requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/user/auth/login"))
                        )
                .logout(n -> n.logoutUrl("/user/logout"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(n -> n
                        .pathMatchers("/manage/**", "/user/**").permitAll());
        return http.build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter() {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(manageAuthenticationManager);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(new JwtHeadersExchangeMatcher());
        bearerAuthenticationFilter.setServerAuthenticationConverter(new MyServerAuthenticationConverter());
        bearerAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        bearerAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
        return bearerAuthenticationFilter;
    }

}

