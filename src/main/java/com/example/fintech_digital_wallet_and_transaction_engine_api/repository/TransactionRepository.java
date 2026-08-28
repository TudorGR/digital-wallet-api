package com.example.fintech_digital_wallet_and_transaction_engine_api.repository;

import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.Transaction;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.TransactionStatus;
import com.example.fintech_digital_wallet_and_transaction_engine_api.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.senderWallet.user.email = :email
            OR t.receiverWallet.user.email = :email)
                    AND (:type IS NULL OR t.type = :type)
                    AND (:status IS NULL OR t.status = :status)
                    AND (CAST(:from AS timestamp) IS NULL OR t.timestamp >= :from)
                    AND (CAST(:to AS timestamp) IS NULL OR t.timestamp < :to)
        """)
    Page<Transaction> findAllFilteredForUser(
            @Param("email") String email,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            Pageable pageable
    );
}