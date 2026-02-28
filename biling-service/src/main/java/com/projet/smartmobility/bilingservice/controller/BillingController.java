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

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingService billingService;

    /**
     * Debiter le compte d'un utilisateur
     * POST /billing/debit
     */
    @PostMapping("/debit")
    public ResponseEntity<BillingDto.TransactionResponse> debit(
            @Valid @RequestBody BillingDto.DebitRequest request) {
        log.info("REST - Debit userId={} amount={}", request.userId(), request.amount());
        return ResponseEntity.ok(billingService.debit(request));
    }

    /**
     * Recharger le compte d'un utilisateur
     * POST /billing/recharge
     */
    @PostMapping("/recharge")
    public ResponseEntity<BillingDto.TransactionResponse> recharge(
            @Valid @RequestBody BillingDto.RechargeRequest request) {
        log.info("REST - Recharge userId={} amount={}", request.userId(), request.amount());
        return ResponseEntity.ok(billingService.recharge(request));
    }

    /**
     * Consulter le solde d'un utilisateur
     * GET /billing/balance/{userId}
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<BillingDto.AccountResponse> getBalance(@PathVariable UUID userId) {
        log.info("REST - Balance userId={}", userId);
        return ResponseEntity.ok(billingService.getBalance(userId));
    }

    /**
     * Historique des transactions d'un utilisateur
     * GET /billing/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BillingDto.TransactionResponse>> getHistory(@PathVariable UUID userId) {
        log.info("REST - History userId={}", userId);
        return ResponseEntity.ok(billingService.getHistory(userId));
    }
}
