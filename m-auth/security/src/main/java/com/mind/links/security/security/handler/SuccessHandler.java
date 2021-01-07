package com.mind.links.security.security.handler;



import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mind.links.common.response.ResponseResult;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * date: 2021-01-07 08:48
 * description
 *
 * @author qiDing
 */
@Component
public class SuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = JWT.create()
                .withClaim("uid", "123456")
                .withIssuedAt(new Date())
                .withIssuer("rkproblem")
                .sign(algorithm);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        System.out.println(jwt.getClaims().entrySet().stream()
                .map(n->n.getKey()+" = " + n.getValue().asString()).collect(Collectors.joining(", ")));
        ResponseResult<String> result =new ResponseResult<>(token);
        DataBuffer buffer = response.bufferFactory().wrap(result.toJsonString().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

