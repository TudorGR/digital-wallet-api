package com.example.fintech_digital_wallet_and_transaction_engine_api.service;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.RegisterRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.UserResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Role;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.User;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Wallet;
import com.example.fintech_digital_wallet_and_transaction_engine_api.mapper.DtoMapper;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.UserRepository;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.WalletRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final DtoMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(dtoMapper::toUserResponse)
                .toList();
    }

    @Transactional()
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user=userRepository.save(User.builder().email(registerRequest.getEmail()).password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER).createdAt(Instant.now()).build());

        walletRepository.save(Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .currency("USD")
                .updatedAt(Instant.now())
                .build());

        return  dtoMapper.toUserResponse(user);
    }

    public UserResponse getUserById(Long id){
        User user=userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User not found"));
        return dtoMapper.toUserResponse(user);
    }

}
