package com.smartmobility.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Base64;

// Ajouts de Aziz — Filtre global corrigé pour préserver les en-têtes B3 — 2026-02-21
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    @Value("${gateway.security.jwt.secret}")
    private String jwtSecret;

    private static final List<String> OPEN_ENDPOINTS = List.of("/actuator/health", "/fallback/pricing");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();
        if (OPEN_ENDPOINTS.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            System.err.println("JWT Validation Error: Missing Authorization Header for path: " + path);
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("JWT Validation Error: Invalid Authorization Header format for path: " + path);
            return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            byte[] decodedKey;
            try {
                // Essayer de décoder en Base64 en premier (recommandé pour HS256)
                decodedKey = Base64.getDecoder().decode(jwtSecret);
            } catch (IllegalArgumentException e) {
                // Fallback: utiliser directement les bytes si non Base64
                decodedKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
            }

            Key key = Keys.hmacShaKeyFor(decodedKey);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                System.err.println("JWT Validation Error: Token Expired for path: " + path);
                return onError(exchange, "Token Expired", HttpStatus.UNAUTHORIZED);
            }

            String userId = claims.getSubject();

            // On s'assure de ne pas écraser les autres headers (Zipkin X-B3-*, Host, etc.)
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            System.err.println("JWT Validation Error: Invalid Token signature or structure for path: " + path + " - "
                    + e.getMessage());
            return onError(exchange, "Invalid Token signature or structure", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = String.format("{\"error\": \"%s\"}", err);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
