package com.projet.smartmobility.bilingservice.client;

import com.projet.smartmobility.bilingservice.dto.MobilityPassResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Fallback Resilience4J pour MobilityPassClient.
 * Déclenché quand user-mobility-pass-service est indisponible.
 * Toutes les méthodes lèvent une exception métier pour bloquer la transaction
 * — on ne peut PAS débiter sans confirmation du service propriétaire du solde.
 */
@Component
@Slf4j
public class MobilityPassClientFallback implements MobilityPassClient {

    @Override
    public MobilityPassResponseDto getByPassNumber(String passNumber) {
        log.error("[FALLBACK] user-mobility-pass-service indisponible — impossible de récupérer le pass {}", passNumber);
        throw new com.projet.smartmobility.bilingservice.exception.ServiceUnavailableException(
                "Le service de gestion des pass est temporairement indisponible. Veuillez réessayer."
        );
    }

    @Override
    public MobilityPassResponseDto getByUserId(UUID userId) {
        log.error("[FALLBACK] user-mobility-pass-service indisponible — impossible de récupérer le pass de l'user {}", userId);
        throw new com.projet.smartmobility.bilingservice.exception.ServiceUnavailableException(
                "Le service de gestion des pass est temporairement indisponible. Veuillez réessayer."
        );
    }

    @Override
    public MobilityPassResponseDto debit(String passNumber, BigDecimal amount) {
        log.error("[FALLBACK] user-mobility-pass-service indisponible — débit IMPOSSIBLE pour le pass {}, montant {}", passNumber, amount);
        throw new com.projet.smartmobility.bilingservice.exception.ServiceUnavailableException(
                "Impossible d'effectuer le débit : le service de gestion des pass est temporairement indisponible."
        );
    }

    @Override
    public MobilityPassResponseDto recharge(String passNumber, BigDecimal amount) {
        log.error("[FALLBACK] user-mobility-pass-service indisponible — rechargement IMPOSSIBLE pour le pass {}", passNumber);
        throw new com.projet.smartmobility.bilingservice.exception.ServiceUnavailableException(
                "Impossible d'effectuer le rechargement : le service de gestion des pass est temporairement indisponible."
        );
    }
}