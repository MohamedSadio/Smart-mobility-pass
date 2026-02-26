package com.smartmobility.trip.service;

import com.smartmobility.trip.dto.TripDto;
import com.smartmobility.trip.entity.Trip;
import com.smartmobility.trip.entity.TripStatus;
import com.smartmobility.trip.exception.TripNotFoundException;
import com.smartmobility.trip.mapper.TripMapper;
import com.smartmobility.trip.repository.TripRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private static final BigDecimal FALLBACK_RATE_PER_KM = BigDecimal.valueOf(125);

    private final TripRepository  tripRepository;
    private final TripMapper      tripMapper;
    private final WebClient.Builder webClientBuilder;

    @Value("${pricing.service.url:http://pricing-discount-service}")
    private String pricingServiceUrl;

    @Transactional
    public TripDto.TripResponse createTrip(TripDto.TripRequest request) {
        log.info("[TRIP] Creating trip - userId={}, transport={}, distance={}km",
                request.getUserId(), request.getTransportType(), request.getDistanceKm());

        Trip trip = tripMapper.toEntity(request);

        // Call pricing service with circuit breaker
        TripDto.PricingResponse pricing = fetchPricing(request);

        trip.setBaseFare(pricing.getBaseFare());
        trip.setFinalFare(pricing.getFinalFare());
        trip.setStatus(TripStatus.CREATED);

        Trip saved = tripRepository.save(trip);
        log.info("[TRIP] Trip saved - id={}, baseFare={}, finalFare={}", saved.getId(), saved.getBaseFare(), saved.getFinalFare());

        // Simulate billing stub
        boolean billed = simulateBilling(saved);
        saved.setStatus(billed ? TripStatus.PAID : TripStatus.FAILED);
        Trip finalTrip = tripRepository.save(saved);

        log.info("[TRIP] Trip finalized - id={}, status={}", finalTrip.getId(), finalTrip.getStatus());
        return tripMapper.toResponse(finalTrip);
    }

    @CircuitBreaker(name = "pricingService", fallbackMethod = "pricingFallback")
    public TripDto.PricingResponse fetchPricing(TripDto.TripRequest request) {
        log.info("[TRIP] Calling pricing-discount-service for userId={}", request.getUserId());

        TripDto.PricingRequest pricingReq = TripDto.PricingRequest.builder()
                .userId(request.getUserId())
                .transportType(request.getTransportType().name())
                .distanceKm(request.getDistanceKm())
                .build();

        TripDto.PricingResponse response = webClientBuilder.build()
                .post()
                .uri(pricingServiceUrl + "/pricing/calculate")
                .bodyValue(pricingReq)
                .retrieve()
                .bodyToMono(TripDto.PricingResponse.class)
                .block();

        log.info("[TRIP] Pricing received - baseFare={}, finalFare={}",
                response != null ? response.getBaseFare() : "null",
                response != null ? response.getFinalFare() : "null");

        return response;
    }

    public TripDto.PricingResponse pricingFallback(TripDto.TripRequest request, Throwable t) {
        log.warn("[TRIP] Pricing service unavailable ({}), applying fallback rate {}FCFA/km",
                t.getMessage(), FALLBACK_RATE_PER_KM);
        BigDecimal base = FALLBACK_RATE_PER_KM.multiply(request.getDistanceKm());
        return TripDto.PricingResponse.builder()
                .baseFare(base)
                .discount(BigDecimal.ZERO)
                .finalFare(base)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TripDto.TripResponse> getTripsByUserId(UUID userId) {
        log.info("[TRIP] Fetching trips for userId={}", userId);
        List<Trip> trips = tripRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (trips.isEmpty()) {
            throw new TripNotFoundException("Aucun trajet trouvé pour userId: " + userId);
        }
        log.info("[TRIP] Found {} trips for userId={}", trips.size(), userId);
        return tripMapper.toResponseList(trips);
    }

    private boolean simulateBilling(Trip trip) {
        // Billing service stub — remplacer par un vrai appel REST
        log.info("[BILLING-STUB] Debiting {}FCFA for tripId={}, userId={}",
                trip.getFinalFare(), trip.getId(), trip.getUserId());
        return true;
    }
}
