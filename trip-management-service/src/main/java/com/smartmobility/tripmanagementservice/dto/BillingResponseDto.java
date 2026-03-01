package com.smartmobility.tripmanagementservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Miroir de la réponse du billing-service après débit.
 */
public record BillingResponseDto(
        UUID          transactionId,
        String        passNumber,
        UUID          userId,
        String        type,
        BigDecimal    amount,
        BigDecimal    newBalance,
        String        passStatus,
        LocalDateTime createdAt
) {}