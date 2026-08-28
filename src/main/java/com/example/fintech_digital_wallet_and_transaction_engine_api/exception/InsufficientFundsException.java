package com.example.fintech_digital_wallet_and_transaction_engine_api.exception;

public class InsufficientFundsException extends RuntimeException{

    public  InsufficientFundsException(String message) {
        super(message);
    }
}
