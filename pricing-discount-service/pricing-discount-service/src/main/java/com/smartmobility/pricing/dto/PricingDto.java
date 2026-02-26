package com.smartmobility.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        @NotNull(message = "userId est obligatoire")
        private UUID userId;

        @NotBlank(message = "transportType est obligatoire")
        private String transportType;

        @NotNull(message = "distanceKm est obligatoire")
        @DecimalMin(value = "0.1", message = "La distance doit être supérieure à 0")
        private BigDecimal distanceKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingResponse {
        private BigDecimal baseFare;
        private BigDecimal discount;
        private BigDecimal finalFare;
        private boolean offPeakApplied;
        private boolean loyaltyApplied;
        private boolean capApplied;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiError {
        private int status;
        private String message;
        private Object errors;
        private LocalDateTime timestamp;
    }
}
