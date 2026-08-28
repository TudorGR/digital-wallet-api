package com.example.fintech_digital_wallet_and_transaction_engine_api.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) { }
