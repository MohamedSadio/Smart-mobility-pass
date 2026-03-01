package com.smartmobility.notificationservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event générique reçu depuis RabbitMQ.
 * Publié par le billing-service (routing key: notification.transaction)
 * et le trip-management-service (routing key: notification.trip)
 */
public record NotificationEvent(
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