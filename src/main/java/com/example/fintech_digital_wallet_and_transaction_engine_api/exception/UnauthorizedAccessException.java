package com.example.fintech_digital_wallet_and_transaction_engine_api.exception;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
