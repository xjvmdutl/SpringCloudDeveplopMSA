package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> { // 해당 Factory를 구현해야한다.
    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) ->{
            //Global Pre Filter
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Global PRE Filter baseMessage: {}", config.getBaseMessage());
            if(config.isPreLogger()){
                log.info("Global Filter Start: request id -> {}", request.getId());
            }
            //Global Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if(config.isPostLogger()){
                    log.info("Global Filter End: response id -> {}", response.getStatusCode());
                }
            }));
        });
    }

    @Data
    public static class Config{
        //Configure 정보를 입력한다.
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
