package com.smartmobility.usermobilitypassservice.service;

import com.smartmobility.usermobilitypassservice.dto.BalanceResponse;
import com.smartmobility.usermobilitypassservice.dto.MobilityPassDTO;
import com.smartmobility.usermobilitypassservice.entity.*;
import com.smartmobility.usermobilitypassservice.exception.InvalidOperationException;
import com.smartmobility.usermobilitypassservice.exception.ResourceNotFoundException;
import com.smartmobility.usermobilitypassservice.exception.ValidationException;
import com.smartmobility.usermobilitypassservice.mapper.MobilityPassMapper;
import com.smartmobility.usermobilitypassservice.repository.MobilityPassRepository;
import com.smartmobility.usermobilitypassservice.repository.UserRepository;
import com.smartmobility.usermobilitypassservice.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MobilityPassService {

    private final MobilityPassRepository mobilityPassRepository;
    private final UserRepository userRepository;
    private final MobilityPassMapper mobilityPassMapper;

    private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("500");

    @Transactional
    public MobilityPassDTO createMobilityPass(UUID userId) {
        log.info("Création d'un Mobility Pass pour l'utilisateur: {}", userId);

        if (userId == null) {
            throw new ValidationException("L'ID utilisateur est obligatoire");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        if (mobilityPassRepository.findByUserId(userId).isPresent()) {
            throw new InvalidOperationException("L'utilisateur possède déjà un Mobility Pass");
        }

        MobilityPass mobilityPass = new MobilityPass();
        mobilityPass.setUser(user);
        mobilityPass.setBalance(BigDecimal.ZERO);
        mobilityPass.setStatus(PassStatus.ACTIVE);
        mobilityPass.setSubscriptionType(SubscriptionType.NONE);
        mobilityPass.setLoyaltyPoints(0);

        MobilityPass savedPass = mobilityPassRepository.save(mobilityPass);

        log.info("Mobility Pass créé avec succès: {}", savedPass.getPassNumber());

        return mobilityPassMapper.toDto(savedPass);
    }

    public MobilityPassDTO getMobilityPassById(UUID id) {
        log.info("Recherche du Mobility Pass avec l'ID: {}", id);

        if (id == null) {
            throw new ValidationException("L'ID du pass est obligatoire");
        }

        MobilityPass pass = mobilityPassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mobility Pass non trouvé avec l'ID: " + id));
        return mobilityPassMapper.toDto(pass);
    }

    public MobilityPassDTO getMobilityPassByPassNumber(String passNumber) {
        log.info("Recherche du Mobility Pass: {}", passNumber);

        ValidationUtils.validateNotEmpty(passNumber, "Numéro de pass");

        MobilityPass pass = mobilityPassRepository.findByPassNumber(passNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Mobility Pass non trouvé: " + passNumber));
        return mobilityPassMapper.toDto(pass);
    }

    public MobilityPassDTO getMobilityPassByUserId(UUID userId) {
        log.info("Recherche du Mobility Pass de l'utilisateur: {}", userId);

        if (userId == null) {
            throw new ValidationException("L'ID utilisateur est obligatoire");
        }

        MobilityPass pass = mobilityPassRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun Mobility Pass trouvé pour cet utilisateur"));
        return mobilityPassMapper.toDto(pass);
    }

    public BalanceResponse getBalance(String passNumber) {
        log.info("Consultation du solde pour le pass: {}", passNumber);

        ValidationUtils.validateNotEmpty(passNumber, "Numéro de pass");

        MobilityPass pass = mobilityPassRepository.findByPassNumber(passNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Mobility Pass non trouvé: " + passNumber));

        BalanceResponse response = new BalanceResponse();
        response.setPassNumber(pass.getPassNumber());
        response.setBalance(pass.getBalance());
        response.setStatus(pass.getStatus().name());

        if (pass.getBalance().compareTo(LOW_BALANCE_THRESHOLD) < 0) {
            response.setMessage("Attention: Solde faible. Veuillez recharger votre pass.");
        } else {
            response.setMessage("Solde suffisant");
        }

        return response;
    }

    @Transactional
    public MobilityPassDTO suspendPass(String passNumber) {
        log.info("Suspension du Mobility Pass: {}", passNumber);

        ValidationUtils.validateNotEmpty(passNumber, "Numéro de pass");

        MobilityPass pass = mobilityPassRepository.findByPassNumber(passNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Mobility Pass non trouvé: " + passNumber));

        if (pass.getStatus() == PassStatus.SUSPENDED) {
            throw new InvalidOperationException("Le pass est déjà suspendu");
        }

        pass.setStatus(PassStatus.SUSPENDED);
        MobilityPass updatedPass = mobilityPassRepository.save(pass);

        log.info("Pass suspendu avec succès");
        return mobilityPassMapper.toDto(updatedPass);
    }

    @Transactional
    public MobilityPassDTO reactivatePass(String passNumber) {
        log.info("Réactivation du Mobility Pass: {}", passNumber);

        ValidationUtils.validateNotEmpty(passNumber, "Numéro de pass");

        MobilityPass pass = mobilityPassRepository.findByPassNumber(passNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Mobility Pass non trouvé: " + passNumber));

        if (pass.getStatus() == PassStatus.ACTIVE) {
            throw new InvalidOperationException("Le pass est déjà actif");
        }

        if (pass.getStatus() == PassStatus.EXPIRED) {
            throw new InvalidOperationException("Impossible de réactiver un pass expiré");
        }

        pass.setStatus(PassStatus.ACTIVE);
        MobilityPass updatedPass = mobilityPassRepository.save(pass);

        log.info("Pass réactivé avec succès");
        return mobilityPassMapper.toDto(updatedPass);
    }

    @Transactional
    public MobilityPassDTO updateSubscription(String passNumber, SubscriptionType subscriptionType) {
        log.info("Mise à jour de l'abonnement pour le pass: {} vers {}", passNumber, subscriptionType);

        ValidationUtils.validateNotEmpty(passNumber, "Numéro de pass");

        if (subscriptionType == null) {
            throw new ValidationException("Le type d'abonnement est obligatoire");
        }

        MobilityPass pass = mobilityPassRepository.findByPassNumber(passNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Mobility Pass non trouvé: " + passNumber));

        if (pass.getStatus() != PassStatus.ACTIVE) {
            throw new InvalidOperationException("Impossible de modifier l'abonnement d'un pass non actif");
        }

        pass.setSubscriptionType(subscriptionType);

        if (subscriptionType != SubscriptionType.NONE) {
            pass.setSubscriptionStartDate(LocalDate.now());

            if (subscriptionType == SubscriptionType.MONTHLY) {
                pass.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
            } else if (subscriptionType == SubscriptionType.ANNUAL) {
                pass.setSubscriptionEndDate(LocalDate.now().plusYears(1));
            }
        } else {
            pass.setSubscriptionStartDate(null);
            pass.setSubscriptionEndDate(null);
        }

        MobilityPass updatedPass = mobilityPassRepository.save(pass);

        log.info("Abonnement mis à jour avec succès");
        return mobilityPassMapper.toDto(updatedPass);
    }

    public List<MobilityPassDTO> getAllPasses() {
        log.info("Récupération de tous les Mobility Pass");
        return mobilityPassRepository.findAll().stream()
                .map(mobilityPassMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MobilityPassDTO> getPassesByStatus(PassStatus status) {
        log.info("Récupération des pass avec le statut: {}", status);

        if (status == null) {
            throw new ValidationException("Le statut est obligatoire");
        }

        return mobilityPassRepository.findByStatus(status).stream()
                .map(mobilityPassMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MobilityPassDTO> getLowBalancePasses() {
        log.info("Recherche des pass avec un solde faible");
        return mobilityPassRepository.findByBalanceLessThanAndStatus(LOW_BALANCE_THRESHOLD, PassStatus.ACTIVE)
                .stream()
                .map(mobilityPassMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MobilityPassDTO> getExpiredSubscriptions() {
        log.info("Recherche des abonnements expirés");
        return mobilityPassRepository.findExpiredSubscriptions().stream()
                .map(mobilityPassMapper::toDto)
                .collect(Collectors.toList());
    }
}