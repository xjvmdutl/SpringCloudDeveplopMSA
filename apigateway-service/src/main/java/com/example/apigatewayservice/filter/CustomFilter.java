package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> { // 해당 Factory를 구현해야한다.
    public CustomFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) ->{
            //Custom Pre Filter
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Custom PRE Filter : request id -> {}", request.getId());

            //Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(()->{ //비동기 방식을 단일값을 리턴할떄 쓴다(MONO)
                log.info("Custom POST filter: response code -> {} ", response.getStatusCode());
            }));
        });
    }


    public static class Config{
        //Configure 정보를 입력한다.
    }
}
