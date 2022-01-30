package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {
    //@Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        //Application.yml에서 동작시킨 것을 JAVA 파일로 옮겼다
        return builder.routes() //라우트에 필요한 정보를 라우트에 넣어주면 된다.
                .route(
                    r -> r.path("/first-service/**")
                        .filters(
                            f -> f.addRequestHeader("first-request", "first-request-header")
                            .addResponseHeader("first-response", "first-response-header")
                        )
                        .uri("http://localhost:8081")
                ).route(
                        r -> r.path("/second-service/**")
                                .filters(
                                        f -> f.addRequestHeader("second-request", "second-request-header")
                                                .addResponseHeader("second-response", "second-response-header")
                                )
                                .uri("http://localhost:8082")
                )
                .build();
    }
}
