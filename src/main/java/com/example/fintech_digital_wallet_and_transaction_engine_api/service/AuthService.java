package com.example.fintech_digital_wallet_and_transaction_engine_api.service;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.AuthResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.LoginRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.UnauthorizedAccessException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        }catch (BadCredentialsException e){
            throw new UnauthorizedAccessException("Invalid email or password");
        }
        String token=jwtUtils.generateToken(request.getEmail());
        return AuthResponse.builder().token(token).build();
    }
}
