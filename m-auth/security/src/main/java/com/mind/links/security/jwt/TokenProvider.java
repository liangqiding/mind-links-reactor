package com.mind.links.security.jwt;

import io.jsonwebtoken.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
    private long tokenValidityInMilliseconds;

    @PostConstruct
    public void init() {
        this.secretKey = encoder.encodeToString(SALT_KEY.getBytes(StandardCharsets.UTF_8));
        this.tokenValidityInMilliseconds = 1000 * TOKEN_VALIDITY;
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.debug("createToken:" + authorities);
        Date validity = new Date((new Date()).getTime() + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        if (StringUtils.isEmpty(token) || !validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        Object o = claims.get(AUTHORITIES_KEY);
        log.debug("getAuthentication:" + o);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature trace :", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token trace: ", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token trace: ", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token trace: ", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid trace: ", e);
        }
        return false;
    }
}
