package com.smartmobility.tripmanagementservice.client.fallback;

import com.smartmobility.tripmanagementservice.client.BillingClient;
import com.smartmobility.tripmanagementservice.dto.BillingResponseDto;
import com.smartmobility.tripmanagementservice.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BillingClientFallback implements BillingClient {

    @Override
    public BillingResponseDto debit(Map<String, Object> request) {
        throw new ServiceUnavailableException(
                "Le service de facturation est indisponible. Le débit ne peut pas être effectué.");
    }
}