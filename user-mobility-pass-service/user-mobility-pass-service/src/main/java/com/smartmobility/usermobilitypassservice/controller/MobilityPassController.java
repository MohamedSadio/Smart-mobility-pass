package com.smartmobility.usermobilitypassservice.controller;

import com.smartmobility.usermobilitypassservice.dto.BalanceResponse;
import com.smartmobility.usermobilitypassservice.dto.MobilityPassDTO;
import com.smartmobility.usermobilitypassservice.entity.PassStatus;
import com.smartmobility.usermobilitypassservice.entity.SubscriptionType;
import com.smartmobility.usermobilitypassservice.service.MobilityPassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mobility-passes")
@RequiredArgsConstructor
@Slf4j
public class MobilityPassController {

    private final MobilityPassService mobilityPassService;

    /**
     * Créer un Mobility Pass pour un utilisateur
     * POST /api/mobility-passes/user/{userId}
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<MobilityPassDTO> createMobilityPass(@PathVariable UUID userId) {
        log.info("REST - Création d'un Mobility Pass pour l'utilisateur: {}", userId);
        MobilityPassDTO pass = mobilityPassService.createMobilityPass(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(pass);
    }

    /**
     * Récupérer un Mobility Pass par ID
     * GET /api/mobility-passes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MobilityPassDTO> getMobilityPassById(@PathVariable UUID id) {
        log.info("REST - Récupération du Mobility Pass: {}", id);
        MobilityPassDTO pass = mobilityPassService.getMobilityPassById(id);
        return ResponseEntity.ok(pass);
    }

    /**
     * Récupérer un Mobility Pass par numéro de pass
     * GET /api/mobility-passes/number/{passNumber}
     */
    @GetMapping("/number/{passNumber}")
    public ResponseEntity<MobilityPassDTO> getMobilityPassByPassNumber(@PathVariable String passNumber) {
        log.info("REST - Récupération du Mobility Pass par numéro: {}", passNumber);
        MobilityPassDTO pass = mobilityPassService.getMobilityPassByPassNumber(passNumber);
        return ResponseEntity.ok(pass);
    }

    /**
     * Récupérer le Mobility Pass d'un utilisateur
     * GET /api/mobility-passes/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<MobilityPassDTO> getMobilityPassByUserId(@PathVariable UUID userId) {
        log.info("REST - Récupération du Mobility Pass de l'utilisateur: {}", userId);
        MobilityPassDTO pass = mobilityPassService.getMobilityPassByUserId(userId);
        return ResponseEntity.ok(pass);
    }

    /**
     * Consulter le solde d'un pass
     * GET /api/mobility-passes/{passNumber}/balance
     */
    @GetMapping("/{passNumber}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String passNumber) {
        log.info("REST - Consultation du solde pour le pass: {}", passNumber);
        BalanceResponse balance = mobilityPassService.getBalance(passNumber);
        return ResponseEntity.ok(balance);
    }

    /**
     * Récupérer tous les Mobility Pass
     * GET /api/mobility-passes
     */
    @GetMapping
    public ResponseEntity<List<MobilityPassDTO>> getAllPasses() {
        log.info("REST - Récupération de tous les Mobility Pass");
        List<MobilityPassDTO> passes = mobilityPassService.getAllPasses();
        return ResponseEntity.ok(passes);
    }

    /**
     * Récupérer les pass par statut
     * GET /api/mobility-passes/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MobilityPassDTO>> getPassesByStatus(@PathVariable PassStatus status) {
        log.info("REST - Récupération des pass avec le statut: {}", status);
        List<MobilityPassDTO> passes = mobilityPassService.getPassesByStatus(status);
        return ResponseEntity.ok(passes);
    }

    /**
     * Récupérer les pass avec un solde faible
     * GET /api/mobility-passes/low-balance
     */
    @GetMapping("/low-balance")
    public ResponseEntity<List<MobilityPassDTO>> getLowBalancePasses() {
        log.info("REST - Récupération des pass avec solde faible");
        List<MobilityPassDTO> passes = mobilityPassService.getLowBalancePasses();
        return ResponseEntity.ok(passes);
    }

    /**
     * Récupérer les abonnements expirés
     * GET /api/mobility-passes/expired-subscriptions
     */
    @GetMapping("/expired-subscriptions")
    public ResponseEntity<List<MobilityPassDTO>> getExpiredSubscriptions() {
        log.info("REST - Récupération des abonnements expirés");
        List<MobilityPassDTO> passes = mobilityPassService.getExpiredSubscriptions();
        return ResponseEntity.ok(passes);
    }

    /**
     * Suspendre un pass
     * PATCH /api/mobility-passes/{passNumber}/suspend
     */
    @PatchMapping("/{passNumber}/suspend")
    public ResponseEntity<MobilityPassDTO> suspendPass(@PathVariable String passNumber) {
        log.info("REST - Suspension du pass: {}", passNumber);
        MobilityPassDTO pass = mobilityPassService.suspendPass(passNumber);
        return ResponseEntity.ok(pass);
    }

    /**
     * Réactiver un pass
     * PATCH /api/mobility-passes/{passNumber}/reactivate
     */
    @PatchMapping("/{passNumber}/reactivate")
    public ResponseEntity<MobilityPassDTO> reactivatePass(@PathVariable String passNumber) {
        log.info("REST - Réactivation du pass: {}", passNumber);
        MobilityPassDTO pass = mobilityPassService.reactivatePass(passNumber);
        return ResponseEntity.ok(pass);
    }

    /**
     * Mettre à jour l'abonnement d'un pass
     * PATCH /api/mobility-passes/{passNumber}/subscription
     */
    @PatchMapping("/{passNumber}/subscription")
    public ResponseEntity<MobilityPassDTO> updateSubscription(
            @PathVariable String passNumber,
            @RequestParam SubscriptionType subscriptionType) {
        log.info("REST - Mise à jour de l'abonnement du pass {} vers {}", passNumber, subscriptionType);
        MobilityPassDTO pass = mobilityPassService.updateSubscription(passNumber, subscriptionType);
        return ResponseEntity.ok(pass);
    }
}