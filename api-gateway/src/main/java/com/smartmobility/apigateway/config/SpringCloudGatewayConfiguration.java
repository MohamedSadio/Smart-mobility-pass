package com.smartmobility.apigateway.config;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class SpringCloudGatewayConfiguration {
    @Bean
    RouteLocator gatewayRouter (RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f
                                .addRequestHeader("myHeaderParam","headerParamValue")
                                .addRequestParameter("myParam","paramValue"))
                        .uri("http://httpbin.org:80"))
                .route(p->p
                        .path("/user-mobility-pass/**")
                        .uri("lb://user-mobility-pass-service"))
                .route(p->p
                        .path("/pricing-discount/**")
                        .uri("lb://pricing-discount-service"))

                .build();
    }
}
