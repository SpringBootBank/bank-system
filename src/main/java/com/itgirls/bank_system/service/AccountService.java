package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        log.info("Создание счета: {}", accountDto);
        Account account = modelMapper.map(accountDto, Account.class);
        account = accountRepository.save(account);
        log.info("Создан новый счет с ID: {}", account.getId());
        return modelMapper.map(account, AccountDto.class);
    }

    public List<AccountDto> getFilteredAccounts(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance) {
        log.info("Фильтр счетов: accountNumber = {}, minBalance = {}, maxBalance = {}", accountNumber, minBalance, maxBalance);
        List<Account> accounts;

        if (accountNumber != null && minBalance != null && maxBalance != null) {
            accounts = accountRepository.findByAccountNumberAndBalanceBetween(accountNumber, minBalance, maxBalance);
        } else if (accountNumber != null) {
            accounts = accountRepository.findByAccountNumber(accountNumber);
        } else if (minBalance != null && maxBalance != null) {
            accounts = accountRepository.findByBalanceBetween(minBalance, maxBalance);
        } else {
            accounts = accountRepository.findAll();
        }

        log.info("Найдено {} счетов", accounts.size());
        return accounts.stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        log.info("Обновление счета с ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        modelMapper.map(accountDto, account);
        account = accountRepository.save(account);
        log.info("Обновлен счет с ID: {}", account.getId());
        return modelMapper.map(account, AccountDto.class);
    }

    @Transactional
    public void deleteAccount(Long id) {
        log.info("Удаление счета с ID: {}", id);
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }

    public AccountDto getAccountById(Long id) {
        log.info("Получение счета с ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return modelMapper.map(account, AccountDto.class);
    }
}

