package com.smartmobility.pricing.controller;

import com.smartmobility.pricing.dto.PricingDto;
import com.smartmobility.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<PricingDto.PricingResponse> calculatePrice(
            @RequestBody PricingDto.PricingRequest request) {
        log.info("REST - Calcul de tarif: userId={}, transport={}, distance={}km",
                request.getUserId(), request.getTransportType(), request.getDistanceKm());

        PricingDto.PricingResponse response = pricingService.calculatePrice(request);

        return ResponseEntity.ok(response);
    }
}