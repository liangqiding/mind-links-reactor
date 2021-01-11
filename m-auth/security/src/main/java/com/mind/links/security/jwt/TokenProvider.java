package com.mind.links.security.jwt;

import com.mind.links.security.config.LinksAuthException;
import io.jsonwebtoken.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


/**
 * date: 2021-01-05 08:48
 * description token管理
 *
 * @author qiDing
 */
@Component
@Slf4j
@ApiModel("token提供者")
public class TokenProvider {

    @ApiModelProperty("盐")
    private static final String SALT_KEY = "links";

    @ApiModelProperty("令牌有效期")
    private static final int TOKEN_VALIDITY = 86400;

    @ApiModelProperty("权限密钥")
    private static final String AUTHORITIES_KEY = "auth";

    @ApiModelProperty("Base64 加密")
    private final Base64.Encoder encoder = Base64.getEncoder();

    @ApiModelProperty("Base64 密钥")
    private String secretKey;

    @ApiModelProperty("令牌有效性（以毫秒为单位）")
    private final long tokenValidityInMilliseconds = 1000 * TOKEN_VALIDITY;

    @PostConstruct
    public void init() {
        this.secretKey = encoder.encodeToString(SALT_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token
     */
    public Mono<String> createToken(Authentication authentication, String clientId) {
        log.debug("createToken:" + authentication + ",clientId=" + clientId);
        Date validity = new Date((new Date()).getTime() + this.tokenValidityInMilliseconds);
        return Mono.just(authentication)
                .map(a -> authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .map(authorities -> Jwts.builder()
                        .setSubject(authentication.getName())
                        .claim(AUTHORITIES_KEY, authorities)
                        .signWith(SignatureAlgorithm.HS512, secretKey)
                        .setExpiration(validity)
                        .compact());
    }

    /**
     * 校验token
     */
    public Mono<Authentication> getAuthentication(String token) {
        return Mono.just(token)
                .filter(this::validateToken)
                .switchIfEmpty(LinksAuthException.errors("无效的token"))
                .map(token0 -> Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token0).getBody())
                .map(claims -> {
                    Collection<? extends GrantedAuthority> authorities =
                            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    return new UsernamePasswordAuthenticationToken(new User(claims.getSubject(), "", authorities), token, authorities);
                });
    }

    public Mono<Authentication> getAuthentication(Claims claims, String token) {
        return Mono.just(claims)
                .map(claims0 -> Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .zipWith(Mono.just(claims), (authorities, claims0) ->
                        new UsernamePasswordAuthenticationToken(new User(claims0.getSubject(), "", authorities), token, authorities));
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            log.error("无效的token："+authToken);
        }
        return false;
    }
}
