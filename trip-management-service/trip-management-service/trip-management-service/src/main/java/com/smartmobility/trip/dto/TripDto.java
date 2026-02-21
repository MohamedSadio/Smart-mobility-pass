package com.smartmobility.trip.dto;

import com.smartmobility.trip.entity.TransportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TripDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripRequest {

        @NotNull(message = "userId est obligatoire")
        private UUID userId;

        @NotNull(message = "transportType est obligatoire")
        private TransportType transportType;

        @NotNull(message = "distanceKm est obligatoire")
        @DecimalMin(value = "0.1", message = "La distance doit être supérieure à 0")
        private BigDecimal distanceKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripResponse {
        private UUID id;
        private UUID userId;
        private TransportType transportType;
        private BigDecimal distanceKm;
        private BigDecimal baseFare;
        private BigDecimal finalFare;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingRequest {
        private UUID userId;
        private String transportType;
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
