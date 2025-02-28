package com.itgirls.bank_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.itgirls.bank_system.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_number", unique = true, nullable = false)
    private String transactionNumber;

    @Column(name = "transaction_type", nullable = false)
    @NotBlank(message = "введите тип транзакции")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @ManyToOne
    @JoinColumn(name = "sender_account_id", referencedColumnName = "id", nullable = false)
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "beneficiary_account_id", referencedColumnName = "id", nullable = false)
    private Account beneficiaryAccount;

    @PrePersist
    public void generateTransactionNumber() {
        if (transactionNumber == null) {
            this.transactionNumber = String.valueOf(System.currentTimeMillis());
        }
    }
}
