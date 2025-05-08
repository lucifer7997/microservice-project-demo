package com.example.apigateway.config;

import com.example.apigateway.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthFilter authFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://auth-service"))
                .route("account-service", r -> r.path("/api/v1/account/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://account-service"))
                .route("department-service", r -> r.path("/api/v1/department/**")
                        .uri("lb://department-service"))
                .build();
    }
}
