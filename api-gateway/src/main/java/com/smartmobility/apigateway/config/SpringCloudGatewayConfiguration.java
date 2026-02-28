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
                        // On retire "/pricing-discount" du chemin avant d'envoyer au microservice
                        .filters(f -> f.rewritePath("/user-mobility-pass/(?<segment>.*)", "/${segment}"))
                        .uri("lb://user-mobility-pass-service"))
                .route(p->p
                        .path("/pricing-discount/**")
                        // On retire "/pricing-discount" du chemin avant d'envoyer au microservice
                        .filters(f -> f.rewritePath("/pricing-discount/(?<segment>.*)", "/${segment}"))
                        .uri("lb://pricing-discount-service"))
                .route(p->p
                        .path("/billing/**")
                        // On retire "/pricing-discount" du chemin avant d'envoyer au microservice
                        .filters(f -> f.rewritePath("/billing/(?<segment>.*)", "/${segment}"))
                        .uri("lb://billing-service"))

                .build();
    }
}
