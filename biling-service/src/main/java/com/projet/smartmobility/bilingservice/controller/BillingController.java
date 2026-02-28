package com.projet.smartmobility.bilingservice.controller;

import com.projet.smartmobility.bilingservice.dto.BillingDto;
import com.projet.smartmobility.bilingservice.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * BillingController — WORKFLOW CORRIGÉ
 *
 * Le billing-service expose des endpoints pour :
 *   - POST /api/billing/debit       → débite le MobilityPass via user-mobility-pass-service
 *   - POST /api/billing/recharge    → recharge le MobilityPass via user-mobility-pass-service
 *   - GET  /api/billing/balance/{passNumber}        → solde live depuis user-mobility-pass-service
 *   - GET  /api/billing/balance/user/{userId}       → solde live par userId
 *   - GET  /api/billing/history/{passNumber}        → historique local des transactions
 *   - GET  /api/billing/history/user/{userId}       → historique local par userId
 */
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingService billingService;

    // -------------------------------------------------------------------------
    // Débit — appelé par TripManagementService après calcul du tarif
    // -------------------------------------------------------------------------

    /**
     * POST /api/billing/debit
     * Corps : { "passNumber": "SMP-...", "amount": 1500.00 }
     *
     * Délègue le débit au user-mobility-pass-service puis enregistre la transaction.
     */
    @PostMapping("/debit")
    public ResponseEntity<BillingDto.TransactionResponse> debit(
            @Valid @RequestBody BillingDto.DebitRequest request) {
        log.info("[CONTROLLER] POST /api/billing/debit — pass={}, montant={}",
                request.passNumber(), request.amount());
        return ResponseEntity.ok(billingService.debit(request));
    }

    // -------------------------------------------------------------------------
    // Rechargement
    // -------------------------------------------------------------------------

    /**
     * POST /api/billing/recharge
     * Corps : { "passNumber": "SMP-...", "amount": 5000.00 }
     */
    @PostMapping("/recharge")
    public ResponseEntity<BillingDto.TransactionResponse> recharge(
            @Valid @RequestBody BillingDto.RechargeRequest request) {
        log.info("[CONTROLLER] POST /api/billing/recharge — pass={}, montant={}",
                request.passNumber(), request.amount());
        return ResponseEntity.ok(billingService.recharge(request));
    }

    // -------------------------------------------------------------------------
    // Consultation du solde (live depuis user-mobility-pass-service)
    // -------------------------------------------------------------------------

    /**
     * GET /api/billing/balance/{passNumber}
     * Retourne le solde en temps réel depuis user-mobility-pass-service.
     */
    @GetMapping("/balance/{passNumber}")
    public ResponseEntity<BillingDto.BalanceResponse> getBalance(
            @PathVariable String passNumber) {
        log.info("[CONTROLLER] GET /api/billing/balance/{}", passNumber);
        return ResponseEntity.ok(billingService.getBalance(passNumber));
    }

    /**
     * GET /api/billing/balance/user/{userId}
     * Retourne le solde en temps réel depuis user-mobility-pass-service via userId.
     */
    @GetMapping("/balance/user/{userId}")
    public ResponseEntity<BillingDto.BalanceResponse> getBalanceByUserId(
            @PathVariable UUID userId) {
        log.info("[CONTROLLER] GET /api/billing/balance/user/{}", userId);
        return ResponseEntity.ok(billingService.getBalanceByUserId(userId));
    }

    
    /**
     * GET /api/billing/history/{passNumber}
     * Retourne l'historique des transactions pour un passNumber donné.
     */
    @GetMapping("/history/{passNumber}")
    public ResponseEntity<List<BillingDto.TransactionResponse>> getHistoryByPassNumber(
            @PathVariable String passNumber) {
        log.info("[CONTROLLER] GET /api/billing/history/{}", passNumber);
        return ResponseEntity.ok(billingService.getHistoryByPassNumber(passNumber));
    }

    /**
     * GET /api/billing/history/user/{userId}
     * Retourne l'historique des transactions pour un userId donné.
     */
    @GetMapping("/history/user/{userId}")
    public ResponseEntity<List<BillingDto.TransactionResponse>> getHistoryByUserId(
            @PathVariable UUID userId) {
        log.info("[CONTROLLER] GET /api/billing/history/user/{}", userId);
        return ResponseEntity.ok(billingService.getHistoryByUserId(userId));
    }
}