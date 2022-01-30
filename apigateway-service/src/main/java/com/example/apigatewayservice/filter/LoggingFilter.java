package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        /*
        return ((exchange, chain) ->{
            //Logging Pre Filter
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Logging PRE Filter baseMessage: {}", config.getBaseMessage());
            if(config.isPreLogger()){
                log.info("Logging Filter Start: request id -> {}", request.getId());
            }
            //Logging Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if(config.isPostLogger()){
                    log.info("Logging Filter End: response id -> {}", response.getStatusCode());
                }
            }));
        });
         */
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain)->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Logging PRE Filter baseMessage: {}", config.getBaseMessage());
            if(config.isPreLogger()){
                log.info("Logging Filter Start: request id -> {}", request.getId());
            }
            //Logging Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                if (config.isPostLogger()) {
                    log.info("Logging Filter End: response id -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE); //순서를 입력 받는다.
        return filter;
    }

    @Data
    public static class Config{
        //Configure 정보를 입력한다.
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
