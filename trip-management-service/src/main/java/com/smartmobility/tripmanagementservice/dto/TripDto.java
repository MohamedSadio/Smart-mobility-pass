package com.smartmobility.tripmanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TripDto {

    /**
     * Requête envoyée par le client via l'API Gateway.
     */
    public record TripRequest(
            @NotBlank(message = "Le numéro de pass est obligatoire")
            String passNumber,

            @NotBlank(message = "Le type de transport est obligatoire")
            String transportType,   // "BUS", "BRT", "TER"

            @NotBlank(message = "La station de départ est obligatoire")
            String startStation,

            @NotBlank(message = "La station d'arrivée est obligatoire")
            String endStation
    ) {}

    /**
     * Réponse retournée au client après enregistrement du trajet.
     */
    public record TripResponse(
            UUID          tripId,
            String        passNumber,
            UUID          userId,
            String        transportType,
            String        startStation,
            String        endStation,
            Double        distanceKm,
            BigDecimal    baseFare,
            BigDecimal    discount,
            BigDecimal    finalFare,
            String        status,
            String        failureReason,
            LocalDateTime createdAt
    ) {}
}