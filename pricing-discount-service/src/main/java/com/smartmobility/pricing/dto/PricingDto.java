package com.smartmobility.pricing.dto;

import com.smartmobility.pricing.enums.SubscriptionType;
import com.smartmobility.pricing.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PricingDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingRequest {
        private UUID userId;
        private TransportType transportType;
        private Double distanceKm;
        private String passNumber;
        private LocalDateTime tripTime;
        private Integer loyaltyPoints;
        private SubscriptionType subscriptionType;
        private BigDecimal dailySpentAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingResponse {
        private UUID userId;
        private TransportType transportType;
        private Double distanceKm;

        // Détails du calcul
        private BigDecimal baseFare;
        private BigDecimal distanceFare;
        private BigDecimal totalBeforeDiscount;

        // Réductions
        private BigDecimal offPeakDiscount;
        private BigDecimal loyaltyDiscount;
        private BigDecimal subscriptionDiscount;
        private BigDecimal totalDiscount;

        // Résultat
        private BigDecimal finalFare;

        // Méta-informations
        private Boolean dailyCapReached;
        private String loyaltyTier;
        private String message;
        private LocalDateTime calculatedAt;
    }
}