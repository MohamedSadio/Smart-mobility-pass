package com.projet.smartmobility.bilingservice.dto;

import com.projet.smartmobility.bilingservice.entity.PassStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTOs du Billing Service.
 *
 * WORKFLOW CORRIGÉ :
 *  - Le billing-service NE stocke PLUS de solde (pas de classe Account).
 *  - Il délègue le débit/rechargement au user-mobility-pass-service via Feign.
 *  - Il enregistre uniquement l'historique des transactions en local.
 *
 *  La requête de débit identifie le pass par son passNumber (passé par le TripService),
 *  conformément au diagramme de séquence : POST /api/billing/debit { passNumber, amount }.
 */
public class BillingDto {

    // -------------------------------------------------------------------------
    // Requêtes entrantes
    // -------------------------------------------------------------------------

    /**
     * Requête de débit envoyée par le TripManagementService.
     * Contient le passNumber (identifiant métier du MobilityPass) et le montant à débiter.
     */
    public record DebitRequest(
            @NotBlank(message = "Le numéro de pass est obligatoire")
            String passNumber,

            @NotNull(message = "Le montant est obligatoire")
            @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
            BigDecimal amount
    ) {}

    /**
     * Requête de rechargement.
     * Peut être émise par un client front-end ou un service de paiement.
     */
    public record RechargeRequest(
            @NotBlank(message = "Le numéro de pass est obligatoire")
            String passNumber,

            @NotNull(message = "Le montant est obligatoire")
            @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
            BigDecimal amount
    ) {}

    // -------------------------------------------------------------------------
    // Réponses sortantes
    // -------------------------------------------------------------------------

    /**
     * Réponse après un débit ou un rechargement.
     * Retourne les informations de la transaction enregistrée + le nouveau solde du pass
     * (tel que retourné par le user-mobility-pass-service).
     */
    public record TransactionResponse(
            UUID transactionId,
            String passNumber,
            UUID userId,
            String type,             // "DEBIT" ou "RECHARGE"
            BigDecimal amount,
            BigDecimal newBalance,   // Solde après opération (retourné par user-mobility-pass-service)
            PassStatus passStatus,       // Statut du pass après opération
            LocalDateTime createdAt
    ) {}

    /**
     * Réponse pour la consultation du solde.
     * Données live depuis le user-mobility-pass-service (pas de cache local).
     */
    public record BalanceResponse(
            String passNumber,
            UUID userId,
            BigDecimal balance,
            PassStatus passStatus,
            Integer loyaltyPoints
    ) {}
}