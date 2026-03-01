package com.smartmobility.tripmanagementservice.client;

import com.smartmobility.tripmanagementservice.client.fallback.PricingClientFallback;
import com.smartmobility.tripmanagementservice.dto.PricingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "pricing-discount-service",
        fallback = PricingClientFallback.class
)
public interface PricingClient {

    @PostMapping("/api/pricing/calculate")
    PricingResponseDto calculate(
            @RequestParam String transportType,
            @RequestParam String passNumber,
            @RequestParam Integer loyaltyPoints
    );
}