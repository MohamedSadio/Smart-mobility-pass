package com.projet.smartmobility.bilingservice.event;

import com.projet.smartmobility.bilingservice.entity.PassStatus;
import com.projet.smartmobility.bilingservice.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Événement publié sur RabbitMQ après chaque transaction.
 * Consommé notamment par le Notification Service pour alertes solde faible.
 */
public record TransactionEvent(
        UUID transactionId,
        String passNumber,
        UUID userId,
        BigDecimal amount,
        BigDecimal balanceAfter,
        TransactionType type,           // "DEBIT" ou "RECHARGE"
        PassStatus passStatus,
        LocalDateTime occurredAt
) {}