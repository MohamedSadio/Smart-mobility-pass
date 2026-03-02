package com.smartmobility.tripmanagementservice.controller;

import com.smartmobility.tripmanagementservice.dto.TripDto;
import com.smartmobility.tripmanagementservice.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Slf4j
public class TripController {

    private final TripService tripService;

    /** POST /api/trips/process — enregistrer un trajet */
    @PostMapping("/process")
    public ResponseEntity<TripDto.TripResponse> processTrip(
            @Valid @RequestBody TripDto.TripRequest request) {
        log.info("[CONTROLLER] POST /api/trips/process — userId={}", request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.registerTrip(request));
    }

    /** GET /api/trips/pass/{passNumber} — historique par pass */
    @GetMapping("/pass/{passNumber}")
    public ResponseEntity<List<TripDto.TripResponse>> getByPassNumber(
            @PathVariable String passNumber) {
        return ResponseEntity.ok(tripService.getHistoryByPassNumber(passNumber));
    }

    /** GET /api/trips/user/{userId} — historique par utilisateur */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripDto.TripResponse>> getByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(tripService.getHistoryByUserId(userId));
    }
}
