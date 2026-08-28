package com.example.fintech_digital_wallet_and_transaction_engine_api.controller;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.LoginRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.AuthResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.RegisterRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.UserResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse createdUser = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
