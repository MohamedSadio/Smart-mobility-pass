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
                        .filters(f -> f.rewritePath("/user-mobility-pass/(?<segment>.*)", "/${segment}"))
                        .uri("lb://user-mobility-pass-service"))
                .route(p->p
                        .path("/pricing-discount/**")
                        .filters(f -> f.rewritePath("/pricing-discount/(?<segment>.*)", "/${segment}"))
                        .uri("lb://pricing-discount-service"))
                .route(p->p
                        .path("/billing/**")
                        .filters(f -> f.rewritePath("/billing/(?<segment>.*)", "/${segment}"))
                        .uri("lb://billing-service"))
                .route(p->p
                        .path("/notification/**")
                        .filters(f -> f.rewritePath("/notification/(?<segment>.*)", "/${segment}"))
                        .uri("lb://notification-service"))
                .route(p->p
                        .path("/trip-management/**")
                        .filters(f -> f.rewritePath("/trip-management/(?<segment>.*)", "/${segment}"))
                        .uri("lb://trip-management-service"))
                .route(p -> p
                        .path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/api/auth/${segment}"))
                        .uri("lb://auth-service"))

                .build();
    }
}
