package com.example.fintech_digital_wallet_and_transaction_engine_api.service;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.DepositRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.WalletResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Wallet;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.ResourceNotFoundException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.UnauthorizedAccessException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.mapper.DtoMapper;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.WalletRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final DtoMapper dtoMapper;
    private final WalletRepository walletRepository;

    public WalletResponse getWalletById(Long id) {
        Wallet wallet=walletRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Wallet not found"));
        return dtoMapper.toWalletResponse(wallet);
    }

    public WalletResponse getWalletByUserEmail(String email) {
        Wallet wallet=walletRepository.findByUserEmail(email).orElseThrow(()->new ResourceNotFoundException("Wallet not found"));
        return dtoMapper.toWalletResponse(wallet);
    }

    public WalletResponse getWalletByUserId(Long userId) {
        Wallet  wallet=walletRepository.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("Wallet not found"));
        return dtoMapper.toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse deposit(@Valid DepositRequest depositRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Wallet wallet=walletRepository.findById(depositRequest.getWalletId()).orElseThrow(()->new ResourceNotFoundException("Wallet not found"));

        if(!wallet.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Can only deposit in own wallet");
        }

        wallet.setBalance(wallet.getBalance().add(depositRequest.getAmount()));
        wallet.setUpdatedAt(Instant.now());

        Wallet savedWallet=walletRepository.save(wallet);
        return dtoMapper.toWalletResponse(savedWallet);
    }
}
