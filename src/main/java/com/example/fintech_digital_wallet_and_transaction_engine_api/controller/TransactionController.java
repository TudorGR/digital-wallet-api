package com.example.fintech_digital_wallet_and_transaction_engine_api.controller;

import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransactionResponse;
import com.example.fintech_digital_wallet_and_transaction_engine_api.dto.TransferRequest;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.TransactionStatus;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.TransactionType;
import com.example.fintech_digital_wallet_and_transaction_engine_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transfers and transaction history")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
        @Operation(summary = "Transfer funds", description = "Transfer funds between wallets.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer")
        })
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(transactionService.transferFunds(transferRequest));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List transactions", description = "Return a filtered, paginated transaction history.")
    @ApiResponse(responseCode = "200", description = "Transactions returned")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(required=false)LocalDate from,
            @RequestParam(required=false)LocalDate to,
            @RequestParam(required=false) TransactionType type,
            @RequestParam(required=false) TransactionStatus status,
            @PageableDefault(page=0,size=10,sort="timestamp",direction = Sort.Direction.DESC)Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions(from,to,type,status,pageable));
    }
}
