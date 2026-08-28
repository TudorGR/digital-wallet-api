package com.example.fintech_digital_wallet_and_transaction_engine_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {

    @NotNull(message = "Wallet ID is required")
    private Long walletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1",message = "Deposit amount must be greater then zero")
    private BigDecimal amount;
}
