package com.smartmobility.pricing.util;

import com.smartmobility.pricing.enums.TransportType;
import com.smartmobility.pricing.exception.PricingException;

import java.util.UUID;

public class ValidationUtils {

    /**
     * Valide qu'une valeur n'est pas nulle
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new PricingException(fieldName + " est obligatoire");
        }
    }

    /**
     * Valide un UUID
     */
    public static void validateUserId(UUID userId) {
        validateNotNull(userId, "L'ID utilisateur");
    }

    /**
     * Valide le type de transport
     */
    public static void validateTransportType(TransportType transportType) {
        validateNotNull(transportType, "Le type de transport");
    }

    /**
     * Valide la distance
     */
    public static void validateDistance(Double distanceKm) {
        validateNotNull(distanceKm, "La distance");

        if (distanceKm <= 0) {
            throw new PricingException("La distance doit être supérieure à 0");
        }

        if (distanceKm > 1000) {
            throw new PricingException("La distance ne peut pas dépasser 1000 km");
        }
    }

    /**
     * Valide les points de fidélité
     */
    public static void validateLoyaltyPoints(Integer loyaltyPoints) {
        if (loyaltyPoints != null && loyaltyPoints < 0) {
            throw new PricingException("Les points de fidélité ne peuvent pas être négatifs");
        }
    }

    /**
     * Validation complète d'une requête de pricing
     */
    public static void validatePricingRequest(UUID userId, TransportType transportType, Double distanceKm, Integer loyaltyPoints) {
        validateUserId(userId);
        validateTransportType(transportType);
        validateDistance(distanceKm);
        validateLoyaltyPoints(loyaltyPoints);
    }
}