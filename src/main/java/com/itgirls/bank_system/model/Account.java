package com.itgirls.bank_system.model;

import com.itgirls.bank_system.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Set<Loan> loans;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Deposit> deposits;

    @OneToMany(mappedBy = "senderAccount", fetch = FetchType.LAZY)
    private Set<Transactions> outgoingTransactions;

    @OneToMany(mappedBy = "beneficiaryAccount", fetch = FetchType.LAZY)
    private Set<Transactions> incomingTransactions;
}