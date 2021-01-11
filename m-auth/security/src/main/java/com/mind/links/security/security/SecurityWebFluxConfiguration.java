package com.mind.links.security.security;


import com.mind.links.security.jwt.JwtHeadersExchangeMatcher;
import com.mind.links.security.jwt.TokenProvider;
import com.mind.links.security.security.handler.FailureHandler;
import com.mind.links.security.security.handler.UnauthorizedAuthenticationEntryPoint;
import com.mind.links.security.security.manage.ManageAuthenticationManager;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * @author qiDing
 * 2020/11/7
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityWebFluxConfiguration {

    private final TokenProvider tokenProvider;

    private final FailureHandler failureHandler;

    private final ManageAuthenticationManager manageAuthenticationManager;

    private final UnauthorizedAuthenticationEntryPoint unauthorizedAuthenticationEntryPoint;

    @ApiModelProperty("白名单")
    private static final String[] AUTH_WHITELIST = {"/user/**","/api/**","/auth/**"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 关闭自带的登录 我们用自己实现的 reactor api 进行登录
     */
    @Bean
    public SecurityWebFilterChain userSecurityFilterChain(ServerHttpSecurity http) {
        http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);

        http
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedAuthenticationEntryPoint))
                .securityMatcher(ServerWebExchangeMatchers.anyExchange())
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(a -> a.pathMatchers(HttpMethod.OPTIONS).permitAll())
                .authorizeExchange(n -> n.pathMatchers(AUTH_WHITELIST).permitAll().anyExchange().authenticated());

        return http.build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter() {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(manageAuthenticationManager);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(new JwtHeadersExchangeMatcher());
        bearerAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter(tokenProvider));
        bearerAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
        return bearerAuthenticationFilter;
    }

}

