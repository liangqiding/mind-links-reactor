package com.mind.links.security.jwt;

import com.mind.links.security.config.LinksAuthException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * date: 2021-01-09 14:43
 * description
 *
 * @author qiDing
 */
@ApiModel("jwt公共信息")
public class TokenCommon {

    @ApiModelProperty("token 前缀")
    public static final String BEARER = "Bearer ";

    @ApiModelProperty("是否包含Bearer前缀")
    public static final Predicate<String> MATCH_BEARER = authValue -> authValue.contains(BEARER);

    @ApiModelProperty("隔离前缀后获取token")
    public static final Function<String, String> ISOLATE_BEARER_VALUE = authValue -> authValue.substring(BEARER.length());

    /**
     * 获取请求头中的token
     */
    public static Mono<String> getToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request)
                .map(r -> r.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .map(ISOLATE_BEARER_VALUE)
                .switchIfEmpty(LinksAuthException.errors("无效的token"));
    }

}
