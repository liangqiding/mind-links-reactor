package com.mind.links.security.config;


import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Mono;

/**
 * 自定义认证异常
 *
 * @author qidingliang
 */
public class LinksAuthException extends AuthenticationException {

    public LinksAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public LinksAuthException(String msg) {
        super(msg);
    }

    public static <T> Mono<T> errors(String msg) {
        return Mono.error(new LinksAuthException(msg));
    }

}
