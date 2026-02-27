package com.smartmobility.pricing.service;

import com.smartmobility.pricing.config.PricingProperties;
import com.smartmobility.pricing.dto.PricingDto;
import com.smartmobility.pricing.enums.LoyaltyTier;
import com.smartmobility.pricing.enums.SubscriptionType;
import com.smartmobility.pricing.enums.TransportType;
import com.smartmobility.pricing.mapper.PricingMapper;
import com.smartmobility.pricing.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingServiceImpl implements PricingService {

    private final PricingProperties pricingProperties;
    private final PricingMapper pricingMapper;

    @Override
    public PricingDto.PricingResponse calculatePrice(PricingDto.PricingRequest request) {
        log.info("Calcul du tarif pour userId={}, transport={}, distance={}km",
                request.getUserId(), request.getTransportType(), request.getDistanceKm());

        // VALIDATION (au début du service)
        validateRequest(request);

        // CALCUL
        PricingMapper.PricingCalculation calculation = performCalculation(request);

        // MAPPING vers DTO Response
        return pricingMapper.toResponse(request, calculation);
    }

    /**
     * Validation complète de la requête
     */
    private void validateRequest(PricingDto.PricingRequest request) {
        ValidationUtils.validateUserId(request.getUserId());
        ValidationUtils.validateTransportType(request.getTransportType());
        ValidationUtils.validateDistance(request.getDistanceKm());
        ValidationUtils.validateLoyaltyPoints(request.getLoyaltyPoints());
    }

    /**
     * Effectue tous les calculs de tarification
     */
    private PricingMapper.PricingCalculation performCalculation(PricingDto.PricingRequest request) {
        // 1. Tarif de base
        BigDecimal baseFare = getBaseFare(request.getTransportType());
        log.debug("Tarif de base: {} FCFA", baseFare);

        // 2. Tarif distance
        BigDecimal distanceFare = calculateDistanceFare(request.getTransportType(), request.getDistanceKm());
        log.debug("Tarif distance: {} FCFA", distanceFare);

        // 3. Total avant réductions
        BigDecimal totalBeforeDiscount = baseFare.add(distanceFare);
        log.debug("Total avant réductions: {} FCFA", totalBeforeDiscount);

        // 4. Calcul des réductions
        BigDecimal offPeakDiscount = calculateOffPeakDiscount(request.getTripTime(), totalBeforeDiscount);
        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(request.getLoyaltyPoints(), totalBeforeDiscount);
        BigDecimal subscriptionDiscount = calculateSubscriptionDiscount(request.getSubscriptionType(), totalBeforeDiscount);

        BigDecimal totalDiscount = offPeakDiscount.add(loyaltyDiscount).add(subscriptionDiscount);
        log.debug("Total des réductions: {} FCFA", totalDiscount);

        // 5. Tarif après réductions
        BigDecimal fareAfterDiscount = totalBeforeDiscount.subtract(totalDiscount);

        // 6. Plafonnement journalier
        boolean dailyCapReached = false;
        BigDecimal finalFare = fareAfterDiscount;

        BigDecimal dailyCap = getDailyCap(request.getTransportType());
        BigDecimal dailySpent = request.getDailySpentAmount() != null ? request.getDailySpentAmount() : BigDecimal.ZERO;
        BigDecimal newDailyTotal = dailySpent.add(fareAfterDiscount);

        if (newDailyTotal.compareTo(dailyCap) > 0) {
            finalFare = dailyCap.subtract(dailySpent);
            if (finalFare.compareTo(BigDecimal.ZERO) < 0) {
                finalFare = BigDecimal.ZERO;
            }
            dailyCapReached = true;
            log.info("Plafonnement journalier atteint: {} FCFA", dailyCap);
        }

        // 7. Construction du résultat
        String loyaltyTier = determineLoyaltyTier(request.getLoyaltyPoints()).name();
        String message = buildMessage(dailyCapReached, offPeakDiscount, loyaltyDiscount, subscriptionDiscount);

        return PricingMapper.PricingCalculation.builder()
                .baseFare(baseFare)
                .distanceFare(distanceFare)
                .totalBeforeDiscount(totalBeforeDiscount)
                .offPeakDiscount(offPeakDiscount)
                .loyaltyDiscount(loyaltyDiscount)
                .subscriptionDiscount(subscriptionDiscount)
                .totalDiscount(totalDiscount)
                .finalFare(finalFare.setScale(2, RoundingMode.HALF_UP))
                .dailyCapReached(dailyCapReached)
                .loyaltyTier(loyaltyTier)
                .message(message)
                .build();
    }

    // ========================================
    // Méthodes de calcul (inchangées)
    // ========================================

    private BigDecimal getBaseFare(TransportType transportType) {
        return pricingProperties.getBaseFare()
                .getOrDefault(transportType.name().toLowerCase(), BigDecimal.ZERO);
    }

    private BigDecimal calculateDistanceFare(TransportType transportType, Double distanceKm) {
        BigDecimal pricePerKm = pricingProperties.getPricePerKm()
                .getOrDefault(transportType.name().toLowerCase(), BigDecimal.ZERO);
        return pricePerKm.multiply(BigDecimal.valueOf(distanceKm));
    }

    private BigDecimal calculateOffPeakDiscount(LocalDateTime tripTime, BigDecimal amount) {
        if (tripTime == null) {
            return BigDecimal.ZERO;
        }

        LocalTime time = tripTime.toLocalTime();
        LocalTime start = pricingProperties.getOffPeak().getStartTime();
        LocalTime end = pricingProperties.getOffPeak().getEndTime();

        if (time.isAfter(start) && time.isBefore(end)) {
            BigDecimal discountPercent = BigDecimal.valueOf(pricingProperties.getDiscount().getOffPeak());
            return amount.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateLoyaltyDiscount(Integer loyaltyPoints, BigDecimal amount) {
        if (loyaltyPoints == null || loyaltyPoints == 0) {
            return BigDecimal.ZERO;
        }

        LoyaltyTier tier = determineLoyaltyTier(loyaltyPoints);
        Integer discountPercent = switch (tier) {
            case GOLD -> pricingProperties.getDiscount().getLoyalty().getGold();
            case SILVER -> pricingProperties.getDiscount().getLoyalty().getSilver();
            case BRONZE -> pricingProperties.getDiscount().getLoyalty().getBronze();
            default -> 0;
        };

        return amount.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSubscriptionDiscount(SubscriptionType subscriptionType, BigDecimal amount) {
        if (subscriptionType == null || subscriptionType == SubscriptionType.NONE) {
            return BigDecimal.ZERO;
        }

        Integer discountPercent = switch (subscriptionType) {
            case MONTHLY -> pricingProperties.getDiscount().getSubscription().getMonthly();
            case ANNUAL -> pricingProperties.getDiscount().getSubscription().getAnnual();
            default -> 0;
        };

        return amount.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private LoyaltyTier determineLoyaltyTier(Integer loyaltyPoints) {
        if (loyaltyPoints == null || loyaltyPoints == 0) {
            return LoyaltyTier.NONE;
        }

        if (loyaltyPoints >= pricingProperties.getLoyalty().getGoldThreshold()) {
            return LoyaltyTier.GOLD;
        } else if (loyaltyPoints >= pricingProperties.getLoyalty().getSilverThreshold()) {
            return LoyaltyTier.SILVER;
        } else if (loyaltyPoints >= pricingProperties.getLoyalty().getBronzeThreshold()) {
            return LoyaltyTier.BRONZE;
        }

        return LoyaltyTier.NONE;
    }

    private BigDecimal getDailyCap(TransportType transportType) {
        return pricingProperties.getDailyCap()
                .getOrDefault(transportType.name().toLowerCase(), BigDecimal.valueOf(10000));
    }

    private String buildMessage(boolean dailyCapReached, BigDecimal offPeak, BigDecimal loyalty, BigDecimal subscription) {
        if (dailyCapReached) {
            return "Plafond journalier atteint - Voyage gratuit";
        }

        StringBuilder message = new StringBuilder();
        if (offPeak.compareTo(BigDecimal.ZERO) > 0) {
            message.append("Réduction heures creuses appliquée. ");
        }
        if (loyalty.compareTo(BigDecimal.ZERO) > 0) {
            message.append("Réduction fidélité appliquée. ");
        }
        if (subscription.compareTo(BigDecimal.ZERO) > 0) {
            message.append("Réduction abonnement appliquée. ");
        }

        if (message.length() == 0) {
            message.append("Tarif standard appliqué");
        }

        return message.toString().trim();
    }
}