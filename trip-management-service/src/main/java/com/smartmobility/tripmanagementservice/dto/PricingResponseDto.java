package com.smartmobility.tripmanagementservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PricingResponseDto(
        UUID       userId,
        String     transportType,
        Double     distanceKm,
        BigDecimal baseFare,
        BigDecimal distanceFare,
        BigDecimal totalBeforeDiscount,
        BigDecimal offPeakDiscount,
        BigDecimal loyaltyDiscount,
        BigDecimal subscriptionDiscount,
        BigDecimal totalDiscount,
        BigDecimal finalFare,
        Boolean    dailyCapReached,
        String     loyaltyTier,
        String     message,
        LocalDateTime calculatedAt
) {}