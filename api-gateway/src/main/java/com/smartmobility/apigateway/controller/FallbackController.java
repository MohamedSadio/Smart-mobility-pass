package com.smartmobility.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/pricing")
    public ResponseEntity<Map<String, String>> pricingFallback() {
        return ResponseEntity.status(503)
                .body(Map.of("message",
                        "Service de tarification temporairement indisponible."));
    }
}
