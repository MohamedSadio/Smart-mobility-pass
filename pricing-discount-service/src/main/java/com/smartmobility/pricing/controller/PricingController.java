package com.smartmobility.pricing.controller;

import com.smartmobility.pricing.dto.PricingDto;
import com.smartmobility.pricing.service.PricingService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
public class PricingController {

    private final PricingService pricingService;

    // Pas besoin de @Autowired ici, Lombok @RequiredArgsConstructor s'en occupe si tu mets 'final'
    private final Environment environment;

    @Retry(name = "pricingRetry", fallbackMethod = "calculatePriceFallback")
    @PostMapping("/calculate")
    public ResponseEntity<PricingDto.PricingResponse> calculatePrice(
            @RequestBody PricingDto.PricingRequest request) {

        String port = environment.getProperty("local.server.port");

        log.info("REST [Instance:{}] - Calcul de tarif: userId={}, transport={}",
                port, request.getUserId(), request.getTransportType());

        PricingDto.PricingResponse response = pricingService.calculatePrice(request);
        response.setExecutionChain("pricing-service instance " + port);

        return ResponseEntity.ok(response);
    }

    /**
     * Méthode de secours (Fallback)
     * Appelée si le calcul échoue après toutes les tentatives de Retry.
     */
    public ResponseEntity<PricingDto.PricingResponse> calculatePriceFallback(
            PricingDto.PricingRequest request, Exception e) {

        log.error("[FALLBACK] Impossible de calculer le tarif pour l'user {}. Erreur: {}",
                request.getUserId(), e.getMessage());

        // On peut renvoyer un tarif "par défaut" ou une erreur 503 propre
        // Ici, on informe le client que le service est indisponible
        return ResponseEntity.status(503).build();
    }
}