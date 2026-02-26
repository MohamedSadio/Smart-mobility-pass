package com.smartmobility.pricing.controller;

import com.smartmobility.pricing.dto.PricingDto;
import com.smartmobility.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
@Slf4j
public class PricingController {

    private final PricingService pricingService;

    /**
     * Calcule le tarif pour un trajet donné.
     * Applique les règles : heures creuses, fidélité, plafonnement journalier.
     */
    @PostMapping("/calculate")
    public ResponseEntity<PricingDto.PricingResponse> calculatePrice(
            @Valid @RequestBody PricingDto.PricingRequest request) {
        log.info("[CONTROLLER] POST /pricing/calculate - userId={}, transport={}, distance={}km",
                request.getUserId(), request.getTransportType(), request.getDistanceKm());
        PricingDto.PricingResponse response = pricingService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }
}
