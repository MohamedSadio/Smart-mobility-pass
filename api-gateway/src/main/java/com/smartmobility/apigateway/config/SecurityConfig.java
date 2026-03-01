package com.smartmobility.apigateway.config;

import com.smartmobility.apigateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchanges -> exchanges

                        // ── Routes publiques ──────────────────────────────
                        .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // ── Routes ADMIN uniquement ───────────────────────
                        .pathMatchers(HttpMethod.GET, "/user-mobility-pass/api/admin/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // ── Routes USER et ADMIN ──────────────────────────
                        .pathMatchers("/trip-management/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/billing/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/notification/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/user-mobility-pass/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/pricing-discount/**").hasAnyRole("USER", "ADMIN")

                        // ── Tout le reste nécessite une authentification ──
                        .anyExchange().authenticated()
                )

                // Ajouter le filtre JWT avant le filtre d'authentification Spring
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}