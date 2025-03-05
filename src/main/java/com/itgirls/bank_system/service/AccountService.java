package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Async
    public CompletableFuture<AccountDto> createAccount(AccountDto accountDto) {
        log.info("Создание счета: {}", accountDto);

        if (accountRepository.findByAccountNumber(accountDto.getAccountNumber())) {
            throw new RuntimeException("Счет с таким номером уже существует");
        }

        Account account = modelMapper.map(accountDto, Account.class);
        account = accountRepository.save(account);
        log.info("Создан новый счет с ID: {}", account.getId());
        return CompletableFuture.completedFuture(modelMapper.map(account, AccountDto.class));
    }

    public List<AccountDto> getFilteredAccounts(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance) {
        log.info("Получение счетов с фильтрами: accountNumber = {}, minBalance = {}, maxBalance = {}", accountNumber, minBalance, maxBalance);
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) {
            throw new RuntimeException("Не найдено счетов");
        }
        return accounts.stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    @Async
    public CompletableFuture<AccountDto> updateAccount(Long id, AccountDto accountDto) {
        log.info("Обновление счета с ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Счет с таким ID не найден"));

        modelMapper.map(accountDto, account);
        account = accountRepository.save(account);
        log.info("Обновлен счет с ID: {}", account.getId());
        return CompletableFuture.completedFuture(modelMapper.map(account, AccountDto.class));
    }

    @Transactional
    public void deleteAccount(Long id) {
        log.info("Удаление счета с ID: {}", id);
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Счет с таким ID не найден");
        }
        accountRepository.deleteById(id);
    }

    public AccountDto getAccountById(Long id) {
        log.info("Получение счета с ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Счет с таким ID не найден"));
        return modelMapper.map(account, AccountDto.class);
    }
}

