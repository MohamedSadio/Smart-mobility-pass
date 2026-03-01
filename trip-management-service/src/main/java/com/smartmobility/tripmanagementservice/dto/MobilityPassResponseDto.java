package com.smartmobility.tripmanagementservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Miroir de la réponse du user-mobility-pass-service.
 * Contient uniquement les champs utiles au trip-management-service.
 */
public record MobilityPassResponseDto(
        UUID       id,
        String     passNumber,
        UUID       userId,
        BigDecimal balance,
        String     status,
        Integer    loyaltyPoints
) {}