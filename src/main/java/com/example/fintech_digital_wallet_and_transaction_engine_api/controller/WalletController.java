package com.example.fintech_digital_wallet_and_transaction_engine_api.controller;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.DepositRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.WalletResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallets", description = "Wallet balance and deposit operations")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my wallet", description = "Return the authenticated user's wallet.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<WalletResponse> getMyWallet(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getWalletByUserEmail(userDetails.getUsername()));
    }

    @PostMapping("/deposit")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deposit funds", description = "Add funds to the authenticated user's wallet.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit completed"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit")
    })
    public ResponseEntity<WalletResponse> deposit(@Valid @RequestBody DepositRequest depositRequest) {
        return ResponseEntity.ok(walletService.deposit(depositRequest));
    }
}
