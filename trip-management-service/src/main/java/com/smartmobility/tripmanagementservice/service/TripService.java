package com.smartmobility.tripmanagementservice.service;

import com.smartmobility.tripmanagementservice.client.BillingClient;
import com.smartmobility.tripmanagementservice.client.MobilityPassClient;
import com.smartmobility.tripmanagementservice.client.PricingClient;
import com.smartmobility.tripmanagementservice.config.RabbitMQConfig;
import com.smartmobility.tripmanagementservice.dto.*;
import com.smartmobility.tripmanagementservice.entity.Trip;
import com.smartmobility.tripmanagementservice.entity.TransportType;
import com.smartmobility.tripmanagementservice.entity.TripStatus;
import com.smartmobility.tripmanagementservice.exception.TripException;
import com.smartmobility.tripmanagementservice.mapper.TripMapper;
import com.smartmobility.tripmanagementservice.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final MobilityPassClient mobilityPassClient;
    private final PricingClient pricingClient;
    private final BillingClient billingClient;
    private final TripMapper tripMapper;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public TripDto.TripResponse registerTrip(TripDto.TripRequest request) {
        log.info("[TRIP] Nouveau trajet — userId={}, transport={}", request.userId(), request.transportType());

        // ── Étape 1 : Vérifier le pass via UserService ───────────────────────
        MobilityPassResponseDto pass = mobilityPassClient.getMobilityPassByUserId(request.userId());

        if (!"ACTIVE".equals(pass.status())) {
            log.warn("[TRIP] Pass inactif — userId={}, status={}", request.userId(), pass.status());
            Trip failed = buildFailedTrip(request, pass.userId(), pass.passNumber(), "PASS_INACTIVE",
                    "Pass inactif ou suspendu. Statut actuel : " + pass.status());
            tripRepository.save(failed);
            publishEvent(failed, "PASS_INACTIVE", pass.status());
            throw new TripException(
                    "Trajet refusé : votre pass est inactif.",
                    "PASS_INACTIVE");
        }

        // ── Étape 2 : Calculer le tarif via PricingService ───────────────────
        double distanceKm = request.distanceKm() != null ? request.distanceKm()
                : getDefaultDistance(request.transportType());

        PricingResponseDto pricing = fetchPricing(pass, request, distanceKm);
        log.info("[TRIP] Tarif calculé — base={}, remise={}, final={}",
                pricing.baseFare(), pricing.totalDiscount(), pricing.finalFare());

        // ── Étape 3 : Vérifier le solde ──────────────────────────────────────
        if (pass.balance().compareTo(pricing.finalFare()) < 0) {
            log.warn("[TRIP] Solde insuffisant — solde={}, requis={}", pass.balance(), pricing.finalFare());
            Trip failed = buildFailedTrip(request, pass.userId(), pass.passNumber(), "INSUFFICIENT_BALANCE",
                    String.format("Solde insuffisant. Solde : %.0f FCFA, Requis : %.0f FCFA",
                            pass.balance(), pricing.finalFare()));
            tripRepository.save(failed);
            publishEvent(failed, "INSUFFICIENT_BALANCE", pass.status());
            throw new TripException(
                    "Trajet refusé : solde insuffisant.",
                    "INSUFFICIENT_BALANCE");
        }

        // ── Étape 4 : Débiter via BillingService ─────────────────────────────
        BillingResponseDto billing = billingClient.debit(Map.of(
                "passNumber", pass.passNumber(),
                "amount", pricing.finalFare()));
        log.info("[TRIP] Débit effectué — txId={}, nouveau solde={}", billing.transactionId(), billing.newBalance());

        // ── Étape 5 : Enregistrer le trajet ──────────────────────────────────
        Trip trip = new Trip();
        trip.setPassNumber(pass.passNumber());
        trip.setUserId(pass.userId());
        trip.setTransportType(TransportType.valueOf(request.transportType()));
        trip.setStartStation("Station A");
        trip.setEndStation("Station B");
        trip.setBaseFare(pricing.baseFare());
        trip.setDiscount(pricing.totalDiscount());
        trip.setFinalFare(pricing.finalFare());
        trip.setStatus(TripStatus.COMPLETED);
        trip.setDistanceKm(distanceKm);
        tripRepository.save(trip);
        log.info("[TRIP] Trajet enregistré — id={}", trip.getId());

        // ── Étape 6 : Publier l'event RabbitMQ ───────────────────────────────
        publishEvent(trip, "TRIP_CONFIRMED", billing.passStatus());

        return tripMapper.toDto(trip);
    }

    @Transactional(readOnly = true)
    public List<TripDto.TripResponse> getHistoryByPassNumber(String passNumber) {
        return tripRepository.findByPassNumberOrderByCreatedAtDesc(passNumber)
                .stream().map(tripMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TripDto.TripResponse> getHistoryByUserId(UUID userId) {
        return tripRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(tripMapper::toDto).toList();
    }

    // ── Méthodes privées ─────────────────────────────────────────────────────

    private Trip buildFailedTrip(TripDto.TripRequest request, UUID userId, String passNumber,
            String reason, String failureReason) {
        Trip trip = new Trip();
        trip.setPassNumber(passNumber);
        trip.setUserId(userId);
        trip.setTransportType(TransportType.valueOf(request.transportType()));
        trip.setStartStation("Station A");
        trip.setEndStation("Station B");
        trip.setStatus(TripStatus.FAILED);
        trip.setFailureReason(failureReason);
        return trip;
    }

    private void publishEvent(Trip trip, String eventType, String passStatus) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_TRIP,
                    tripMapper.toEvent(trip, eventType, passStatus));
            log.info("[TRIP] TripEvent publié — type={}, tripId={}", eventType, trip.getId());
        } catch (Exception e) {
            log.warn("[TRIP] Échec publication RabbitMQ — tripId={} : {}", trip.getId(), e.getMessage());
        }
    }

    private double getDefaultDistance(String transportType) {
        return switch (transportType.toUpperCase()) {
            case "BRT" -> 10.0;
            case "TER" -> 50.0;
            default -> 5.0; // BUS
        };
    }

    @CircuitBreaker(name = "pricingService", fallbackMethod = "pricingFallback")
    public PricingResponseDto fetchPricing(MobilityPassResponseDto pass, TripDto.TripRequest request,
            double distanceKm) {
        return pricingClient.calculate(Map.of(
                "userId", pass.userId(),
                "passNumber", pass.passNumber(),
                "transportType", request.transportType(),
                "loyaltyPoints", pass.loyaltyPoints(),
                "tripTime", java.time.LocalDateTime.now().toString(),
                "distanceKm", distanceKm,
                "dailySpentAmount", BigDecimal.ZERO));
    }

    public PricingResponseDto pricingFallback(MobilityPassResponseDto pass, TripDto.TripRequest request,
            double distanceKm, Throwable t) {
        log.error("[TRIP] PricingService indisponible (Fallback activé) : {}", t.getMessage());
        // Logique de secours demandée : permettre de poursuivre avec un tarif par
        // défaut
        BigDecimal defaultBase = BigDecimal.valueOf(distanceKm * 200.0); // 200 FCFA/km par défaut
        return new PricingResponseDto(
                pass.userId(),
                request.transportType(),
                distanceKm,
                defaultBase, // baseFare
                BigDecimal.ZERO, // distanceFare
                defaultBase, // totalBeforeDiscount
                BigDecimal.ZERO, // offPeakDiscount
                BigDecimal.ZERO, // loyaltyDiscount
                BigDecimal.ZERO, // subscriptionDiscount
                BigDecimal.ZERO, // totalDiscount
                defaultBase, // finalFare
                false, // dailyCapReached
                "UNKNOWN", // loyaltyTier
                "Fallback price", // message
                java.time.LocalDateTime.now() // calculatedAt
        );
    }
}