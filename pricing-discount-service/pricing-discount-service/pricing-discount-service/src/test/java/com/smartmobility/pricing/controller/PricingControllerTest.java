package com.smartmobility.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmobility.pricing.dto.PricingDto;
import com.smartmobility.pricing.exception.GlobalExceptionHandler;
import com.smartmobility.pricing.exception.PricingServiceException;
import com.smartmobility.pricing.service.PricingService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PricingControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private PricingController pricingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pricingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /pricing/calculate → 200 avec tarif standard (pas de réduction)")
    void calculatePrice_standardFare() throws Exception {
        PricingDto.PricingRequest request = PricingDto.PricingRequest.builder()
                .userId(UUID.randomUUID())
                .transportType("BRT")
                .distanceKm(BigDecimal.valueOf(12))
                .build();

        PricingDto.PricingResponse response = PricingDto.PricingResponse.builder()
                .baseFare(BigDecimal.valueOf(1200))
                .discount(BigDecimal.ZERO)
                .finalFare(BigDecimal.valueOf(1200))
                .offPeakApplied(false)
                .loyaltyApplied(false)
                .capApplied(false)
                .build();

        when(pricingService.calculatePrice(any())).thenReturn(response);

        mockMvc.perform(post("/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseFare").value(1200))
                .andExpect(jsonPath("$.discount").value(0))
                .andExpect(jsonPath("$.finalFare").value(1200))
                .andExpect(jsonPath("$.offPeakApplied").value(false));
    }

    @Test
    @DisplayName("POST /pricing/calculate → 200 avec réduction heures creuses")
    void calculatePrice_offPeakDiscount() throws Exception {
        PricingDto.PricingRequest request = PricingDto.PricingRequest.builder()
                .userId(UUID.randomUUID())
                .transportType("BRT")
                .distanceKm(BigDecimal.valueOf(12))
                .build();

        PricingDto.PricingResponse response = PricingDto.PricingResponse.builder()
                .baseFare(BigDecimal.valueOf(1200))
                .discount(BigDecimal.valueOf(120))
                .finalFare(BigDecimal.valueOf(1080))
                .offPeakApplied(true)
                .loyaltyApplied(false)
                .capApplied(false)
                .build();

        when(pricingService.calculatePrice(any())).thenReturn(response);

        mockMvc.perform(post("/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offPeakApplied").value(true))
                .andExpect(jsonPath("$.discount").value(120))
                .andExpect(jsonPath("$.finalFare").value(1080));
    }

    @Test
    @DisplayName("POST /pricing/calculate → 200 avec plafonnement journalier")
    void calculatePrice_withDailyCap() throws Exception {
        PricingDto.PricingRequest request = PricingDto.PricingRequest.builder()
                .userId(UUID.randomUUID())
                .transportType("TER")
                .distanceKm(BigDecimal.valueOf(50))
                .build();

        PricingDto.PricingResponse response = PricingDto.PricingResponse.builder()
                .baseFare(BigDecimal.valueOf(5000))
                .discount(BigDecimal.ZERO)
                .finalFare(BigDecimal.valueOf(3000))
                .offPeakApplied(false)
                .loyaltyApplied(false)
                .capApplied(true)
                .build();

        when(pricingService.calculatePrice(any())).thenReturn(response);

        mockMvc.perform(post("/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capApplied").value(true))
                .andExpect(jsonPath("$.finalFare").value(3000));
    }

    @Test
    @DisplayName("POST /pricing/calculate → 400 validation - champs manquants")
    void calculatePrice_validationFails() throws Exception {
        PricingDto.PricingRequest request = new PricingDto.PricingRequest(); // vide

        mockMvc.perform(post("/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /pricing/calculate → 400 transport type invalide")
    void calculatePrice_invalidTransportType() throws Exception {
        PricingDto.PricingRequest request = PricingDto.PricingRequest.builder()
                .userId(UUID.randomUUID())
                .transportType("INVALID")
                .distanceKm(BigDecimal.valueOf(10))
                .build();

        when(pricingService.calculatePrice(any()))
                .thenThrow(new PricingServiceException("Type de transport invalide: INVALID"));

        mockMvc.perform(post("/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Type de transport invalide: INVALID"));
    }
}
