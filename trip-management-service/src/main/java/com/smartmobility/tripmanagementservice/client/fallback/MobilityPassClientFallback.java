package com.smartmobility.tripmanagementservice.client.fallback;

import com.smartmobility.tripmanagementservice.client.MobilityPassClient;
import com.smartmobility.tripmanagementservice.dto.MobilityPassResponseDto;
import com.smartmobility.tripmanagementservice.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class MobilityPassClientFallback implements MobilityPassClient {

    @Override
    public MobilityPassResponseDto getMobilityPassByPassNumber(String passNumber) {
        throw new ServiceUnavailableException(
                "Le service utilisateur est indisponible. Impossible de vérifier le pass.");
    }
}