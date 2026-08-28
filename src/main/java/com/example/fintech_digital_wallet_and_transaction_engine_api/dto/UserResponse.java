package com.example.fintech_digital_wallet_and_transaction_engine_api.dto;

import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private Instant createdAt;
}
