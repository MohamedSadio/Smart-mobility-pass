package com.smartmobility.tripmanagementservice.client.fallback;

import com.smartmobility.tripmanagementservice.client.PricingClient;
import com.smartmobility.tripmanagementservice.dto.PricingResponseDto;
import com.smartmobility.tripmanagementservice.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class PricingClientFallback implements PricingClient {

    @Override
    public PricingResponseDto calculate(String transportType, String passNumber, Integer loyaltyPoints) {
        throw new ServiceUnavailableException(
                "Le service de tarification est indisponible. Le trajet ne peut pas être traité.");
    }
}