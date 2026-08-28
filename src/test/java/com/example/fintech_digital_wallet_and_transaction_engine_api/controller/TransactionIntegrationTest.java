package com.example.fintech_digital_wallet_and_transaction_engine_api.controller;

import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Role;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.User;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Wallet;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.TransactionRepository;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.UserRepository;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User senderUser;
    private User receiverUser;
    private Wallet senderWallet;
    private Wallet receiverWallet;

    @BeforeEach
    void setUp(){
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        senderUser=userRepository.save(
                User.builder()
                        .email("sender@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.USER)
                .build()
        );
        receiverUser=userRepository.save(
                User.builder()
                        .email("receiver@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.USER)
                .build()
        );
        senderWallet = walletRepository.save(
                Wallet.builder()
                        .balance(new BigDecimal("100.00"))
                        .currency("USD")
                        .user(senderUser)
                        .build()
        );

        receiverWallet = walletRepository.save(
                Wallet.builder()
                        .balance(new BigDecimal("50.00"))
                        .currency("USD")
                        .user(receiverUser)
                        .build()
        );
    }

    @Test
    void login_success() throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "sender@example.com",
                                    "password": "password123"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void transfer_success() throws Exception {
        // Login
        MvcResult loginResult = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "sender@example.com",
                                    "password": "password123"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        );

        String token = loginJson.get("token").asText();

        // Transfer
        mockMvc.perform(
                        post("/api/v1/transactions/transfer")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "senderWalletId": %d,
                                    "receiverWalletId": %d,
                                    "amount": 25.00
                                }
                                """.formatted(
                                        senderWallet.getId(),
                                        receiverWallet.getId()
                                ))
                )
                .andExpect(status().isOk());

        // Verify database
        Wallet updatedSender =
                walletRepository.findById(senderWallet.getId())
                        .orElseThrow();

        Wallet updatedReceiver =
                walletRepository.findById(receiverWallet.getId())
                        .orElseThrow();

        assertEquals(
                new BigDecimal("75.00"),
                updatedSender.getBalance()
        );

        assertEquals(
                new BigDecimal("75.00"),
                updatedReceiver.getBalance()
        );
    }

    @Test
    @DisplayName("Failed transfer rolls back all database balance modifications")
    void transfer_insufficientFunds_rollsBackDatabaseChanges() throws Exception {
        // 1.JWT
        MvcResult loginResult = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "email": "sender@example.com",
                                "password": "password123"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        // 2.invalid transfer ($500.00 > $100.00)
        mockMvc.perform(
                        post("/api/v1/transactions/transfer")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "senderWalletId": %d,
                                "receiverWalletId": %d,
                                "amount": 500.00
                            }
                            """.formatted(senderWallet.getId(), receiverWallet.getId()))
                )
                .andExpect(status().isBadRequest());

        // 3. Verify rolled back
        Wallet updatedSender = walletRepository.findById(senderWallet.getId()).orElseThrow();
        Wallet updatedReceiver = walletRepository.findById(receiverWallet.getId()).orElseThrow();

        assertEquals(new BigDecimal("100.00"), updatedSender.getBalance());
        assertEquals(new BigDecimal("50.00"), updatedReceiver.getBalance());
    }
}
