package com.example.fintech_digital_wallet_and_transaction_engine_api.controller;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.DepositRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.WalletResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.service.WalletService;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getWalletByUserEmail(userDetails.getUsername()));
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(@Valid @RequestBody DepositRequest depositRequest){
        return ResponseEntity.ok(walletService.deposit(depositRequest));
    }
}
