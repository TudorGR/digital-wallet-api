package com.example.fintech_digital_wallet_and_transaction_engine_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Wallet wallet;

    @PrePersist
    public void onCreate(){
        this.createdAt = Instant.now();
    }
}
