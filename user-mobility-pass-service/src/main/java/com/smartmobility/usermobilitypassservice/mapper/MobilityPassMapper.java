package com.smartmobility.usermobilitypassservice.mapper;

import com.smartmobility.usermobilitypassservice.dto.MobilityPassDTO;
import com.smartmobility.usermobilitypassservice.entity.MobilityPass;
import com.smartmobility.usermobilitypassservice.entity.PassStatus;
import com.smartmobility.usermobilitypassservice.entity.SubscriptionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MobilityPassMapper {

    /**
     * Convertit une entité MobilityPass en MobilityPassDTO
     */
    public MobilityPassDTO toDto(MobilityPass pass) {
        if (pass == null) {
            return null;
        }

        MobilityPassDTO dto = new MobilityPassDTO();
        dto.setId(pass.getId());
        dto.setPassNumber(pass.getPassNumber());
        dto.setUserId(pass.getUser() != null ? pass.getUser().getId() : null);
        dto.setBalance(pass.getBalance());
        dto.setStatus(pass.getStatus());
        dto.setSubscriptionType(pass.getSubscriptionType());
        dto.setSubscriptionStartDate(pass.getSubscriptionStartDate());
        dto.setSubscriptionEndDate(pass.getSubscriptionEndDate());
        dto.setLoyaltyPoints(pass.getLoyaltyPoints());
        dto.setCreatedAt(pass.getCreatedAt());
        dto.setLastUsedAt(pass.getLastUsedAt());

        return dto;
    }

    /**
     * Convertit un MobilityPassDTO en entité MobilityPass
     */
    public MobilityPass toEntity(MobilityPassDTO dto) {
        if (dto == null) {
            return null;
        }

        MobilityPass pass = new MobilityPass();
        pass.setId(dto.getId());
        pass.setPassNumber(dto.getPassNumber());
        pass.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        pass.setStatus(dto.getStatus() != null ? dto.getStatus() : PassStatus.ACTIVE);
        pass.setSubscriptionType(dto.getSubscriptionType() != null ? dto.getSubscriptionType() : SubscriptionType.NONE);
        pass.setSubscriptionStartDate(dto.getSubscriptionStartDate());
        pass.setSubscriptionEndDate(dto.getSubscriptionEndDate());
        pass.setLoyaltyPoints(dto.getLoyaltyPoints() != null ? dto.getLoyaltyPoints() : 0);
        pass.setLastUsedAt(dto.getLastUsedAt());

        return pass;
    }

    /**
     * Met à jour une entité MobilityPass existante avec les données d'un DTO
     */
    public void updateEntityFromDto(MobilityPassDTO dto, MobilityPass pass) {
        if (dto == null || pass == null) {
            return;
        }

        if (dto.getBalance() != null) {
            pass.setBalance(dto.getBalance());
        }
        if (dto.getStatus() != null) {
            pass.setStatus(dto.getStatus());
        }
        if (dto.getSubscriptionType() != null) {
            pass.setSubscriptionType(dto.getSubscriptionType());
        }
        if (dto.getSubscriptionStartDate() != null) {
            pass.setSubscriptionStartDate(dto.getSubscriptionStartDate());
        }
        if (dto.getSubscriptionEndDate() != null) {
            pass.setSubscriptionEndDate(dto.getSubscriptionEndDate());
        }
        if (dto.getLoyaltyPoints() != null) {
            pass.setLoyaltyPoints(dto.getLoyaltyPoints());
        }
        if (dto.getLastUsedAt() != null) {
            pass.setLastUsedAt(dto.getLastUsedAt());
        }
    }
}