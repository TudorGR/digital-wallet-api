package com.example.fintech_digital_wallet_and_transaction_engine_api.mapper;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.ErrorResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransactionResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.UserResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.WalletResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Transaction;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.User;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    public UserResponse toUserResponse(User user){
        if(user == null) return null;

        return UserResponse.builder().id(user.getId()).email(user.getEmail()).role(user.getRole()).createdAt(user.getCreatedAt()).build();
    }

    public WalletResponse toWalletResponse(Wallet wallet){
        if(wallet == null) return null;

        return WalletResponse.builder().id(wallet.getId()).balance(wallet.getBalance()).currency(wallet.getCurrency()).userId(wallet.getUser()!=null?wallet.getUser().getId():null).updatedAt(wallet.getUpdatedAt()).build();
    }

    public TransactionResponse toTransactionResponse(Transaction transaction){
        if(transaction == null) return null;

        return TransactionResponse.builder().id(transaction.getId()).senderWalletId(transaction.getSenderWallet() != null ? transaction.getSenderWallet().getId() : null).receiverWalletId(transaction.getReceiverWallet() != null ? transaction.getReceiverWallet().getId() : null).amount(transaction.getAmount()).type(transaction.getType()).status(transaction.getStatus()).timestamp(transaction.getTimestamp()).build();
    }
}
