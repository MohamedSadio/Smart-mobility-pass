package com.smartmobility.notificationservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Référence du format que le trip-management-service
 * doit publier sur routing key "notification.trip"
 */
public record TripEvent(
        UUID          sourceId,
        String        sourceService,
        UUID          userId,
        String        passNumber,
        String        type,
        BigDecimal    amount,
        BigDecimal    balanceAfter,
        String        passStatus,
        String        transportType,
        String        startStation,
        String        endStation,
        LocalDateTime occurredAt
) {}