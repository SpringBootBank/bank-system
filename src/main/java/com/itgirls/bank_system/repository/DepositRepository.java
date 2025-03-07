package com.itgirls.bank_system.repository;

import com.itgirls.bank_system.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

}