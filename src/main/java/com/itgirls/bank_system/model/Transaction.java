package com.itgirls.bank_system.model;

import jakarta.persistence.*;
import lombok.*;
import com.itgirls.bank_system.enums.TransactionType;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id", referencedColumnName = "id", nullable = false)
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_account_id", referencedColumnName = "id", nullable = false)
    private Account beneficiaryAccount;
    }

