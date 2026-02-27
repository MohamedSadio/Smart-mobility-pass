package com.smartmobility.pricing.mapper;

import com.smartmobility.pricing.dto.PricingDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PricingMapper {

    /**
     * Convertit une PricingRequest en objet métier interne (si nécessaire)
     * Pour l'instant, on retourne directement la request car on n'a pas d'entité
     */
    public PricingDto.PricingRequest toRequest(PricingDto.PricingRequest dto) {
        // Dans ce cas simple, on retourne le DTO tel quel
        // Cette méthode est utile si vous avez des transformations à faire
        return dto;
    }

    /**
     * Construit une PricingResponse avec tous les paramètres
     */
    public PricingDto.PricingResponse toResponse(
            PricingDto.PricingRequest request,
            BigDecimal baseFare,
            BigDecimal distanceFare,
            BigDecimal totalBeforeDiscount,
            BigDecimal offPeakDiscount,
            BigDecimal loyaltyDiscount,
            BigDecimal subscriptionDiscount,
            BigDecimal totalDiscount,
            BigDecimal finalFare,
            Boolean dailyCapReached,
            String loyaltyTier,
            String message) {

        return PricingDto.PricingResponse.builder()
                .userId(request.getUserId())
                .transportType(request.getTransportType())
                .distanceKm(request.getDistanceKm())
                .baseFare(baseFare)
                .distanceFare(distanceFare)
                .totalBeforeDiscount(totalBeforeDiscount)
                .offPeakDiscount(offPeakDiscount)
                .loyaltyDiscount(loyaltyDiscount)
                .subscriptionDiscount(subscriptionDiscount)
                .totalDiscount(totalDiscount)
                .finalFare(finalFare)
                .dailyCapReached(dailyCapReached)
                .loyaltyTier(loyaltyTier)
                .message(message)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Version simplifiée avec un objet de calcul intermédiaire
     */
    public PricingDto.PricingResponse toResponse(PricingDto.PricingRequest request, PricingCalculation calculation) {
        return toResponse(
                request,
                calculation.getBaseFare(),
                calculation.getDistanceFare(),
                calculation.getTotalBeforeDiscount(),
                calculation.getOffPeakDiscount(),
                calculation.getLoyaltyDiscount(),
                calculation.getSubscriptionDiscount(),
                calculation.getTotalDiscount(),
                calculation.getFinalFare(),
                calculation.getDailyCapReached(),
                calculation.getLoyaltyTier(),
                calculation.getMessage()
        );
    }

    /**
     * Classe interne pour regrouper les résultats de calcul
     */
    @lombok.Data
    @lombok.Builder
    public static class PricingCalculation {
        private BigDecimal baseFare;
        private BigDecimal distanceFare;
        private BigDecimal totalBeforeDiscount;
        private BigDecimal offPeakDiscount;
        private BigDecimal loyaltyDiscount;
        private BigDecimal subscriptionDiscount;
        private BigDecimal totalDiscount;
        private BigDecimal finalFare;
        private Boolean dailyCapReached;
        private String loyaltyTier;
        private String message;
    }
}