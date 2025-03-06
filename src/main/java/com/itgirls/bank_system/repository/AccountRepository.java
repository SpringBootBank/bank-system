package com.itgirls.bank_system.repository;

import com.itgirls.bank_system.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByAccountNumber(String accountNumber);

    List<Account> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);

    List<Account> findByAccountNumberAndBalanceBetween(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance);
}