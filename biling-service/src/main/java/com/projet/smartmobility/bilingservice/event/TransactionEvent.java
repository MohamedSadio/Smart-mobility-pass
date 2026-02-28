package com.projet.smartmobility.bilingservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionEvent(
                UUID transactionId,
                UUID userId,
                UUID accountId,
                BigDecimal amount,
                String type,
                LocalDateTime occurredAt) {
}
