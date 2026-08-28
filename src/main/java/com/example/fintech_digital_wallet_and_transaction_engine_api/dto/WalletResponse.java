package com.example.fintech_digital_wallet_and_transaction_engine_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;
    private BigDecimal balance;
    private String currency;
    private Long userId;
    private Instant updatedAt;
}
