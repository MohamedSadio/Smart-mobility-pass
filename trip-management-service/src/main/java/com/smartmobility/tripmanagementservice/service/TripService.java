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

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository      tripRepository;
    private final MobilityPassClient  mobilityPassClient;
    private final PricingClient       pricingClient;
    private final BillingClient       billingClient;
    private final TripMapper          tripMapper;
    private final RabbitTemplate      rabbitTemplate;

    @Transactional
    public TripDto.TripResponse registerTrip(TripDto.TripRequest request) {
        log.info("[TRIP] Nouveau trajet — pass={}, transport={}", request.passNumber(), request.transportType());

        // ── Étape 1 : Vérifier le pass via UserService ───────────────────────
        MobilityPassResponseDto pass = mobilityPassClient.getMobilityPassByPassNumber(request.passNumber());

        if (!"ACTIVE".equals(pass.status())) {
            log.warn("[TRIP] Pass inactif — pass={}, status={}", request.passNumber(), pass.status());
            Trip failed = buildFailedTrip(request, pass.userId(), "PASS_INACTIVE",
                    "Pass inactif ou suspendu. Statut actuel : " + pass.status());
            tripRepository.save(failed);
            publishEvent(failed, "PASS_INACTIVE", pass.status());
            throw new TripException(
                    "Trajet refusé : votre pass est inactif.",
                    "PASS_INACTIVE"
            );
        }

        // ── Étape 2 : Calculer le tarif via PricingService ───────────────────
        double distanceKm = getDefaultDistance(request.transportType());

        PricingResponseDto pricing = pricingClient.calculate(Map.of(
                "userId",           pass.userId(),
                "passNumber",       request.passNumber(),
                "transportType",    request.transportType(),
                "loyaltyPoints",    pass.loyaltyPoints(),
                "tripTime",         java.time.LocalDateTime.now().toString(),
                "distanceKm",       distanceKm,
                "dailySpentAmount", java.math.BigDecimal.ZERO
        ));
        log.info("[TRIP] Tarif calculé — base={}, remise={}, final={}",
                pricing.baseFare(), pricing.totalDiscount(), pricing.finalFare());

        // ── Étape 3 : Vérifier le solde ──────────────────────────────────────
        if (pass.balance().compareTo(pricing.finalFare()) < 0) {
            log.warn("[TRIP] Solde insuffisant — solde={}, requis={}", pass.balance(), pricing.finalFare());
            Trip failed = buildFailedTrip(request, pass.userId(), "INSUFFICIENT_BALANCE",
                    String.format("Solde insuffisant. Solde : %.0f FCFA, Requis : %.0f FCFA",
                            pass.balance(), pricing.finalFare()));
            tripRepository.save(failed);
            publishEvent(failed, "INSUFFICIENT_BALANCE", pass.status());
            throw new TripException(
                    "Trajet refusé : solde insuffisant.",
                    "INSUFFICIENT_BALANCE"
            );
        }

        // ── Étape 4 : Débiter via BillingService ─────────────────────────────
        BillingResponseDto billing = billingClient.debit(Map.of(
                "passNumber", request.passNumber(),
                "amount",     pricing.finalFare()
        ));
        log.info("[TRIP] Débit effectué — txId={}, nouveau solde={}", billing.transactionId(), billing.newBalance());

        // ── Étape 5 : Enregistrer le trajet ──────────────────────────────────
        Trip trip = new Trip();
        trip.setPassNumber(request.passNumber());
        trip.setUserId(pass.userId());
        trip.setTransportType(TransportType.valueOf(request.transportType()));
        trip.setStartStation(request.startStation());
        trip.setEndStation(request.endStation());
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

    private Trip buildFailedTrip(TripDto.TripRequest request, UUID userId,
                                 String reason, String failureReason) {
        Trip trip = new Trip();
        trip.setPassNumber(request.passNumber());
        trip.setUserId(userId);
        trip.setTransportType(TransportType.valueOf(request.transportType()));
        trip.setStartStation(request.startStation());
        trip.setEndStation(request.endStation());
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
            default    ->  5.0; // BUS
        };
    }
}