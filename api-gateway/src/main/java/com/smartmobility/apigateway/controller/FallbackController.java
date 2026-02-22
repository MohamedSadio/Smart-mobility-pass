package com.smartmobility.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// Ajouts de Aziz — Contrôleur Fallback pour le Circuit Breaker de Resilience4j — 2026-02-21
@RestController
public class FallbackController {

    @GetMapping("/fallback/pricing")
    public Map<String, Object> fallbackPricing() {
        Map<String, Object> response = new HashMap<>();
        response.put("price", 1.0);
        response.put("note", "fallback-standard-price");
        return response;
    }
}
