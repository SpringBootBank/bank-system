package com.itgirls.bank_system.model;

import com.itgirls.bank_system.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLoan;

    @Column(name = "amount_loan", nullable = false)
    private BigDecimal amountLoan;

    @Column(name = "interest_rate_loan", nullable = false)
    private BigDecimal interestRateLoan;

    @Column(name = "start_date_loan", nullable = false)
    private LocalDate startDateLoan;

    @Column(name = "end_date_loan", nullable = false)
    private LocalDate endDateLoan;

    @Column(name = "monthly_payment_loan", nullable = false)
    private BigDecimal monthlyPayment;

    @Enumerated(EnumType.STRING)
    @Column(name ="status_loan", nullable = false)
    private LoanStatus statusLoan;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}