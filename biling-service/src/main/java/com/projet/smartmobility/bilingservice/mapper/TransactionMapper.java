package com.projet.smartmobility.bilingservice.mapper;

import com.projet.smartmobility.bilingservice.dto.BillingDto;
import com.projet.smartmobility.bilingservice.dto.MobilityPassResponseDto;
import com.projet.smartmobility.bilingservice.entity.Transaction;
import com.projet.smartmobility.bilingservice.entity.TransactionType;
import com.projet.smartmobility.bilingservice.event.TransactionEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    /**
     * Construit une Transaction à partir de la réponse Feign du user-mobility-pass-service.
     */
    public Transaction toEntity(MobilityPassResponseDto pass, java.math.BigDecimal amount, TransactionType type) {
        Transaction tx = new Transaction();
        tx.setPassNumber(pass.getPassNumber());
        tx.setUserId(pass.getUserId());
        tx.setType(type);
        tx.setAmount(amount);
        tx.setBalanceAfter(pass.getBalance());
        tx.setPassStatus(pass.getStatus());
        return tx;
    }

    /**
     * Convertit une Transaction enregistrée en TransactionResponse.
     */
    public BillingDto.TransactionResponse toDto(Transaction tx) {
        return new BillingDto.TransactionResponse(
                tx.getId(),
                tx.getPassNumber(),
                tx.getUserId(),
                tx.getType().name(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getPassStatus(),
                tx.getCreatedAt()
        );
    }

    /**
     * Convertit un MobilityPassResponseDto en BalanceResponse.
     */
    public BillingDto.BalanceResponse toBalanceResponse(MobilityPassResponseDto pass) {
        return new BillingDto.BalanceResponse(
                pass.getPassNumber(),
                pass.getUserId(),
                pass.getBalance(),
                pass.getStatus(),
                pass.getLoyaltyPoints()
        );
    }

    /**
     * Construit un TransactionEvent à publier sur RabbitMQ.
     */
    public TransactionEvent toEvent(Transaction tx) {
        return new TransactionEvent(
                tx.getId(),           // sourceId
                "BILLING",            // sourceService
                tx.getUserId(),
                tx.getPassNumber(),
                tx.getType().name(),  // String au lieu de l'enum
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getPassStatus().name(), // String au lieu de l'enum
                null,                 // transportType
                null,                 // startStation
                null,                 // endStation
                LocalDateTime.now()
        );
    }
}