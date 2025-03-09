package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    List<AccountDto> getFilteredAccounts(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance, String accountType);

    AccountDto updateAccount(Long id, AccountDto accountDto);

    void deleteAccount(Long id);

    AccountDto getAccountById(Long id);

    List<AccountDto> getAccountsByUserId(Long userId);

    AccountDto createAccountForUser(Long userId, AccountDto accountDto);
}


