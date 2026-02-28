package com.smartmobility.usermobilitypassservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {

    private String passNumber;
    private BigDecimal balance;
    private String status;
    private String message;
}