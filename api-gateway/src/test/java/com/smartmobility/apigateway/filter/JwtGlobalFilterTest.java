package com.smartmobility.apigateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Date;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Ajouts de Aziz — Tests unitaires pour la préservation des en-têtes B3 et vérification JWT — 2026-02-21
class JwtGlobalFilterTest {

    private JwtGlobalFilter filter;
    private GatewayFilterChain filterChain;

    private final String secret = Base64.getEncoder()
            .encodeToString("SmartMobilitySecretKeyForJwt2026!AndBeyond".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        filter = new JwtGlobalFilter();
        ReflectionTestUtils.setField(filter, "jwtSecret", secret);
        filterChain = Mockito.mock(GatewayFilterChain.class);
        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void testOpenEndpointsAreIgnored() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
    }

    @Test
    void testMissingAuthorizationHeaderReturns401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/trips")
                .header("X-B3-TraceId", "123456789")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testValidJwtPreservesB3Headers() {
        String token = Jwts.builder()
                .setSubject("user-123")
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)))
                .compact();

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/trips")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("X-B3-TraceId", "trace-id-123")
                .header("X-B3-SpanId", "span-id-456")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(captor.capture());

        ServerWebExchange mutatedExchange = captor.getValue();
        HttpHeaders mutatedHeaders = mutatedExchange.getRequest().getHeaders();

        // Verification that B3 headers are preserved
        assertTrue(mutatedHeaders.containsKey("X-B3-TraceId"));
        assertEquals("trace-id-123", mutatedHeaders.getFirst("X-B3-TraceId"));

        assertTrue(mutatedHeaders.containsKey("X-B3-SpanId"));
        assertEquals("span-id-456", mutatedHeaders.getFirst("X-B3-SpanId"));

        // Verification that X-User-Id is injected
        assertTrue(mutatedHeaders.containsKey("X-User-Id"));
        assertEquals("user-123", mutatedHeaders.getFirst("X-User-Id"));

        // Original headers should also be preserved
        assertTrue(mutatedHeaders.containsKey(HttpHeaders.AUTHORIZATION));
    }
}
