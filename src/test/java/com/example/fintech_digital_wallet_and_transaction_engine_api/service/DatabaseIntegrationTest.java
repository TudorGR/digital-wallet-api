package com.example.fintech_digital_wallet_and_transaction_engine_api.service;

import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Role;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.User;
import com.example.fintech_digital_wallet_and_transaction_engine_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser(){
        User user = User.builder()
                .email("test@example.com")
                .password("hashed-password")
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId(), "User id should be generated after save");
        assertEquals("test@example.com", saved.getEmail(), "Saved email should match input");
        assertEquals(Role.USER, saved.getRole(), "Saved role should match input");
    }
}
