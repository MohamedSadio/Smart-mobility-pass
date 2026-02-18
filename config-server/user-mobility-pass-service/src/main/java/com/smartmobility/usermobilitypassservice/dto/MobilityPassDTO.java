package com.smartmobility.usermobilitypassservice.dto;

import com.smartmobility.usermobilitypassservice.entity.PassStatus;
import com.smartmobility.usermobilitypassservice.entity.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobilityPassDTO {

    private UUID id;
    private String passNumber;
    private UUID userId;
    private BigDecimal balance;
    private PassStatus status;
    private SubscriptionType subscriptionType;
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
    private Integer loyaltyPoints;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;

    public static class UpdateSubscriptionRequest {
    }
}