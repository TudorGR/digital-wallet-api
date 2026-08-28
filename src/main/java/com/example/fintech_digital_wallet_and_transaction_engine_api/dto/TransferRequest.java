package com.example.fintech_digital_wallet_and_transaction_engine_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull(message = "Sender wallet ID is required")
    private Long senderWalletId;

    @NotNull(message = "Receiver wallet ID is required")
    private Long receiverWalletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero")
    private BigDecimal amount;
}
