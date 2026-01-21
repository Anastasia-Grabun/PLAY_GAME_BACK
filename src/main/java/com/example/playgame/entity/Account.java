package com.example.playgame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToOne(mappedBy = "account",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private Credential credential;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "account",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    private List<Bucket> buckets;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "owner",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<Purchase> purchases;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "developer"
    )
    private List<Game> games;
}
