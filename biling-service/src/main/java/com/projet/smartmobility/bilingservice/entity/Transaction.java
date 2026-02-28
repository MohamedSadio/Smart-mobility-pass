package com.projet.smartmobility.bilingservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_pass_number", columnList = "pass_number"),
        @Index(name = "idx_tx_user_id",     columnList = "user_id"),
        @Index(name = "idx_tx_created_at",  columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** Numéro du MobilityPass débité/rechargé (clé métier du user-mobility-pass-service). */
    @Column(name = "pass_number", nullable = false, length = 60)
    private String passNumber;

    /** ID de l'utilisateur propriétaire du pass (dénormalisé pour les requêtes d'historique). */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Type d'opération : DEBIT ou RECHARGE. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    /** Montant de l'opération en FCFA. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

   
    @Column(name = "balance_after", precision = 12, scale = 2)
    private BigDecimal balanceAfter;

    /** Statut du pass au moment de la transaction (ACTIVE, SUSPENDED…). */
    @Column(name = "pass_status", length = 20)
    private PassStatus passStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}