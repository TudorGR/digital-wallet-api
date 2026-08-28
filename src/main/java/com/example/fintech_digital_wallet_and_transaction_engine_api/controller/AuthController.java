package com.example.fintech_digital_wallet_and_transaction_engine_api.controller;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.AuthResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.LoginRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return  ResponseEntity.ok(authService.login(loginRequest));
    }
}
