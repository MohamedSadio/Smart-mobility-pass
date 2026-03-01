package com.smartmobility.tripmanagementservice.dto;

import java.math.BigDecimal;

/**
 * Miroir de la réponse du pricing-discount-service.
 */
public record PricingResponseDto(
        BigDecimal baseFare,
        BigDecimal discount,
        BigDecimal finalFare,
        String     discountReason
) {}