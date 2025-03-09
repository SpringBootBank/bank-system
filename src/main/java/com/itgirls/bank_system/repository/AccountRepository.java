package com.itgirls.bank_system.repository;

import com.itgirls.bank_system.enums.AccountType;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    List<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUser(User user);

    List<Account> findByType(AccountType accountType);
}

