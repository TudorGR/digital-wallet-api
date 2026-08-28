package com.example.fintech_digital_wallet_and_transaction_engine_api.service;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransactionResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransferRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.*;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.InsufficientFundsException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.ResourceNotFoundException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.exception.UnauthorizedAccessException;
import com.example.fintech_digital_wallet_and_transaction_engine_api.mapper.DtoMapper;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.TransactionRepository;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.WalletRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public TransactionResponse transferFunds(TransferRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (request.getSenderWalletId().equals(request.getReceiverWalletId())) {
            throw new IllegalArgumentException("Self-transfer is prohibited");
        }

        Wallet senderWallet=walletRepository.findByIdWithLock(request.getSenderWalletId()).orElseThrow(()->new ResourceNotFoundException("Sender wallet not found"));

        if(!senderWallet.getUser().getEmail().equals(email)){
            throw new UnauthorizedAccessException("Can only transfer funds from your wallet");
        }

        Wallet receiverWallet=walletRepository.findByIdWithLock(request.getReceiverWalletId()).orElseThrow(()->new ResourceNotFoundException("Receiver wallet not found"));

        if(senderWallet.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientFundsException("Amount must be less than sender wallet balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

        senderWallet.setUpdatedAt(Instant.now());
        receiverWallet.setUpdatedAt(Instant.now());

        Transaction transaction = Transaction.builder()
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .timestamp(Instant.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return dtoMapper.toTransactionResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(LocalDate from, LocalDate to, TransactionType type, TransactionStatus status, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Instant fromInstant = (from != null) ? from.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant toInstant = (to != null) ? to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

        return transactionRepository.findAllFilteredForUser(email, fromInstant, toInstant, type, status, pageable)
                .map(dtoMapper::toTransactionResponse);
    }

}
