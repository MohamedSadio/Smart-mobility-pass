package com.smartmobility.pricing.service;

import com.smartmobility.pricing.config.PricingProperties;
import com.smartmobility.pricing.dto.PricingDto;
import com.smartmobility.pricing.exception.PricingServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final PricingProperties props;

    /**
     * Calcule le tarif final d'un trajet selon les règles métier :
     *   1. baseFare = distanceKm * baseRatePerKm
     *   2. Réduction heures creuses (20h-6h) : -10%
     *   3. Réduction fidélité (>10 trajets)  : -5%
     *   4. Plafonnement journalier max 3000 FCFA
     */
    public PricingDto.PricingResponse calculatePrice(PricingDto.PricingRequest request) {
        log.info("[PRICING] Calculating price - userId={}, transport={}, distance={}km",
                request.getUserId(), request.getTransportType(), request.getDistanceKm());

        validateTransportType(request.getTransportType());

        BigDecimal baseFare = props.getBaseRatePerKm()
                .multiply(request.getDistanceKm())
                .setScale(2, RoundingMode.HALF_UP);

        log.debug("[PRICING] baseFare={}FCFA ({}km × {}FCFA/km)",
                baseFare, request.getDistanceKm(), props.getBaseRatePerKm());

        BigDecimal totalDiscount = BigDecimal.ZERO;
        boolean offPeakApplied  = false;
        boolean loyaltyApplied  = false;
        boolean capApplied      = false;

        // 1. Off-peak discount
        if (props.getOffPeak().isEnabled() && isOffPeak()) {
            BigDecimal d = baseFare.multiply(props.getOffPeak().getDiscountRate())
                    .setScale(2, RoundingMode.HALF_UP);
            totalDiscount = totalDiscount.add(d);
            offPeakApplied = true;
            log.debug("[PRICING] Off-peak discount applied: -{}FCFA", d);
        }

        // 2. Loyalty discount — tripCount is passed via request context (stateless)
        // tripCount est transmis par trip-management-service dans le header ou simulé ici
        // Pour un service stateless, on se base sur une valeur passée ou on ignore
        // Ici on simule : si userId hashCode > 0 → >10 trips (à remplacer par header réel)
        if (props.getLoyalty().isEnabled()) {
            int simulatedTripCount = Math.abs(request.getUserId().hashCode() % 20);
            if (simulatedTripCount > props.getLoyalty().getMinTrips()) {
                BigDecimal d = baseFare.multiply(props.getLoyalty().getDiscountRate())
                        .setScale(2, RoundingMode.HALF_UP);
                totalDiscount = totalDiscount.add(d);
                loyaltyApplied = true;
                log.debug("[PRICING] Loyalty discount applied: -{}FCFA (simulated trips={})",
                        d, simulatedTripCount);
            }
        }

        BigDecimal finalFare = baseFare.subtract(totalDiscount).max(BigDecimal.ZERO);

        // 3. Daily cap
        if (finalFare.compareTo(props.getDailyCap()) > 0) {
            finalFare = props.getDailyCap();
            capApplied = true;
            log.debug("[PRICING] Daily cap applied: fare capped at {}FCFA", props.getDailyCap());
        }

        log.info("[PRICING] Result - base={}, discount={}, final={}, offPeak={}, loyalty={}, cap={}",
                baseFare, totalDiscount, finalFare, offPeakApplied, loyaltyApplied, capApplied);

        return PricingDto.PricingResponse.builder()
                .baseFare(baseFare)
                .discount(totalDiscount)
                .finalFare(finalFare)
                .offPeakApplied(offPeakApplied)
                .loyaltyApplied(loyaltyApplied)
                .capApplied(capApplied)
                .build();
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    private boolean isOffPeak() {
        int hour = LocalTime.now().getHour();
        int start = props.getOffPeak().getStartHour(); // 20
        int end   = props.getOffPeak().getEndHour();   // 6
        // Window crosses midnight: 20h–23h OR 0h–6h
        return hour >= start || hour < end;
    }

    private void validateTransportType(String type) {
        try {
            Enum.valueOf(TransportTypeEnum.class, type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PricingServiceException("Type de transport invalide: " + type
                    + ". Valeurs acceptées: BUS, BRT, TER");
        }
    }

    private enum TransportTypeEnum { BUS, BRT, TER }
}
