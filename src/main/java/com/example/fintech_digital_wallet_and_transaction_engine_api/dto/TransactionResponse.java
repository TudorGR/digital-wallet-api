package com.example.fintech_digital_wallet_and_transaction_engine_api.dto;

import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.TransactionStatus;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long senderWalletId;
    private Long receiverWalletId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private Instant timestamp;
}
