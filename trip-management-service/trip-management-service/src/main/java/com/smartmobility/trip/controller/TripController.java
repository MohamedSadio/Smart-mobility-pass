package com.smartmobility.trip.controller;

import com.smartmobility.trip.dto.TripDto;
import com.smartmobility.trip.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
@Slf4j
public class TripController {

    private final TripService tripService;

    /**
     * Crée un nouveau trajet.
     * Appelle pricing-discount-service, applique CircuitBreaker, sauvegarde et débite.
     */
    @PostMapping
    public ResponseEntity<TripDto.TripResponse> createTrip(
            @Valid @RequestBody TripDto.TripRequest request) {
        log.info("[CONTROLLER] POST /trips - userId={}", request.getUserId());
        TripDto.TripResponse response = tripService.createTrip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retourne l'historique des trajets d'un utilisateur, triés par date décroissante.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripDto.TripResponse>> getTripsByUser(
            @PathVariable UUID userId) {
        log.info("[CONTROLLER] GET /trips/user/{}", userId);
        List<TripDto.TripResponse> trips = tripService.getTripsByUserId(userId);
        return ResponseEntity.ok(trips);
    }
}
