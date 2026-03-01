package com.smartmobility.tripmanagementservice.client;

import com.smartmobility.tripmanagementservice.client.fallback.BillingClientFallback;
import com.smartmobility.tripmanagementservice.dto.BillingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "billing-service",
        fallback = BillingClientFallback.class
)
public interface BillingClient {

    @PostMapping("/api/billing/debit")
    BillingResponseDto debit(
            @RequestParam String passNumber,
            @RequestParam java.math.BigDecimal amount
    );
}