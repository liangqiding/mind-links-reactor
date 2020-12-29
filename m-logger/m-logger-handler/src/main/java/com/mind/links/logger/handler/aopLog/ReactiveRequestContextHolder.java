package com.mind.links.logger.handler.aopLog;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-29 09:31
 * @version v1.0.0
 */
public class ReactiveRequestContextHolder {
    static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link }
     *
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(CONTEXT_KEY));
    }

}
