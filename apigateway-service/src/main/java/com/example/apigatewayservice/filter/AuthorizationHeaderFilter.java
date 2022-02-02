package com.example.apigatewayservice.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.apigatewayservice.filter.GlobalFilter.Config;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends
    AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    //login -> token -> users(with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        //토큰이 잘 발행이 되었는지를 판단하는 필터
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey(AUTHORIZATION)) {
                return onError(exchange, "no authorization header", UNAUTHORIZED); //적절한 헤더가 없을 경우
            }
            String authorizationHeader = request.getHeaders().get(AUTHORIZATION)
                .get(0); //토큰을 가지고 온다
            String jwt = authorizationHeader.replace("Bearer", "");
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", UNAUTHORIZED);
            }
            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;
        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                .parseClaimsJws(jwt).getBody()
                .getSubject();
        } catch (Exception ex) {
            returnValue = false;
        }
        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }
        return returnValue;
    }

    //Mono, Flux 는 Spring WebFlux 에서 새로 나온 개념으로 반환되는 타입 개념으로 생각하면 된다(비동기 방식 반환)
    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(error);
        return response.setComplete();
    }

    public static class Config {

    }
}
