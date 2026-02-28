package com.projet.smartmobility.bilingservice.client;

import com.projet.smartmobility.bilingservice.dto.MobilityPassResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;


@FeignClient(
        name = "user-mobility-pass-service",
        fallback = MobilityPassClientFallback.class
)
public interface MobilityPassClient {

    /**
     * Récupère un MobilityPass par son numéro de pass.
     * Utilisé pour vérifier le solde et le statut avant de lancer un débit.
     */
    @GetMapping("/api/mobility-passes/number/{passNumber}")
    MobilityPassResponseDto getByPassNumber(@PathVariable("passNumber") String passNumber);

    /**
     * Récupère le MobilityPass d'un utilisateur par son userId.
     */
    @GetMapping("/api/mobility-passes/user/{userId}")
    MobilityPassResponseDto getByUserId(@PathVariable("userId") UUID userId);

    /**
     * Débite le solde du MobilityPass via le user-mobility-pass-service.
     * C'est ce service qui applique les règles métier :
     *   - vérification statut ACTIVE
     *   - vérification solde suffisant
     *   - mise à jour atomique du solde
     */
    @PostMapping("/api/mobility-passes/{passNumber}/debit")
    MobilityPassResponseDto debit(
            @PathVariable("passNumber") String passNumber,
            @RequestParam("amount") BigDecimal amount
    );

    /**
     * Recharge le solde du MobilityPass via le user-mobility-pass-service.
     */
    @PostMapping("/api/mobility-passes/{passNumber}/recharge")
    MobilityPassResponseDto recharge(
            @PathVariable("passNumber") String passNumber,
            @RequestParam("amount") BigDecimal amount
    );
}