package com.projet.smartmobility.bilingservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionEvent(
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