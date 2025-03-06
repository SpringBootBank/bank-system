package com.itgirls.bank_system.repository;

import com.itgirls.bank_system.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {
   List<Transactions> findByBankUser_Id(Long userId);
}