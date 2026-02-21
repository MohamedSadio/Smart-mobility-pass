package com.smartmobility.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmobility.trip.dto.TripDto;
import com.smartmobility.trip.entity.TransportType;
import com.smartmobility.trip.exception.GlobalExceptionHandler;
import com.smartmobility.trip.exception.TripNotFoundException;
import com.smartmobility.trip.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    private UUID userId;
    private TripDto.TripResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();

        sampleResponse = TripDto.TripResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .transportType(TransportType.BRT)
                .distanceKm(BigDecimal.valueOf(12))
                .baseFare(BigDecimal.valueOf(1500))
                .finalFare(BigDecimal.valueOf(1350))
                .status("PAID")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /trips → 201 CREATED avec réponse valide")
    void createTrip_success() throws Exception {
        TripDto.TripRequest request = TripDto.TripRequest.builder()
                .userId(userId)
                .transportType(TransportType.BRT)
                .distanceKm(BigDecimal.valueOf(12))
                .build();

        when(tripService.createTrip(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.transportType").value("BRT"))
                .andExpect(jsonPath("$.baseFare").value(1500))
                .andExpect(jsonPath("$.finalFare").value(1350))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("POST /trips → 400 quand champs manquants")
    void createTrip_validationFails() throws Exception {
        TripDto.TripRequest request = new TripDto.TripRequest(); // champs vides

        mockMvc.perform(post("/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("GET /trips/user/{userId} → 200 avec liste de trajets")
    void getUserTrips_success() throws Exception {
        when(tripService.getTripsByUserId(userId)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/trips/user/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }

    @Test
    @DisplayName("GET /trips/user/{userId} → 404 quand aucun trajet")
    void getUserTrips_notFound() throws Exception {
        when(tripService.getTripsByUserId(userId))
                .thenThrow(new TripNotFoundException("Aucun trajet trouvé pour userId: " + userId));

        mockMvc.perform(get("/trips/user/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
