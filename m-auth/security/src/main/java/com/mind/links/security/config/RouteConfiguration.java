package com.mind.links.security.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Date: 2020/6/06 0010 13:55
 * Description: TODO 跨域配置
 *
 * @author qiding
 */
@Configuration
@ApiModel("跨域配置")
public class RouteConfiguration {
    @ApiModelProperty("允许的请求头字段")
    private static final String ALLOWED_HEADERS = "*";

    @ApiModelProperty("允许的请求类型")
    private static final String ALLOWED_METHODS = "*";

    @ApiModelProperty("指定允许其他域名访问")
    private static final String ALLOWED_ORIGIN = "*";

    @ApiModelProperty("允许的来源")
    private static final String ALLOWED_EXPOSE = "*";

    @ApiModelProperty("预检结果缓存时间")
    private static final String MAX_AGE = "18000L";

    @ApiModelProperty("是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回")
    private static final String ALLOW_CREDENTIALS = "true";

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
                headers.add("Access-Control-Max-Age", MAX_AGE);
                headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
                headers.add("Access-Control-Expose-Headers", ALLOWED_EXPOSE);
                headers.add("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS);
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }
}

