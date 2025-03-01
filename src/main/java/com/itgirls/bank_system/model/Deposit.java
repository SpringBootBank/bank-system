package com.itgirls.bank_system.model;

import com.itgirls.bank_system.enums.DepositStatus;
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
@Table(name = "deposit")
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="amount_deposit", nullable = false)
    private BigDecimal amountDeposit;

    @Column(name="interest_rate_deposit", nullable = false)
    private BigDecimal interestRateDeposit;

    @Column(name="start_date_deposit", nullable = false)
    private LocalDate startDateDeposit;

    @Column(name="end_date_deposit", nullable = false)
    private LocalDate endDateDeposit;

    @Enumerated(EnumType.STRING)
    @Column(name="status_deposit", nullable = false)
    private DepositStatus statusDeposit;

//    @OneToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account accountDeposit;

}
