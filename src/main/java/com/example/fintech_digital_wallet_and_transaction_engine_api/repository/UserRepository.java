package com.example.fintech_digital_wallet_and_transaction_engine_api.repository;

import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
