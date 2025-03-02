package com.itgirls.bank_system.model;

import com.itgirls.bank_system.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 16)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "senderAccount", fetch = FetchType.LAZY)
    private Set<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "beneficiaryAccount", fetch = FetchType.LAZY)
    private Set<Transaction> incomingTransactions;
}