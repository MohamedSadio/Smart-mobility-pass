package com.projet.smartmobility.bilingservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BillingDto {

        public record DebitRequest(
                        @NotNull UUID userId,
                        @NotNull @DecimalMin("0.01") BigDecimal amount) {
        }

        public record RechargeRequest(
                        @NotNull UUID userId,
                        @NotNull @DecimalMin("0.01") BigDecimal amount) {
        }

        public record TransactionResponse(
                        UUID id,
                        UUID userId,
                        String type,
                        BigDecimal amount,
                        LocalDateTime createdAt) {
        }

        public record AccountResponse(
                        UUID accountId,
                        UUID userId,
                        BigDecimal balance,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        }
}
