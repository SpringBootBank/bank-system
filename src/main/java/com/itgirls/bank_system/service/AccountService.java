package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.UserRepository;
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
    private final UserRepository userRepository;
//    private final DepositRepository depositRepository;

    public AccountDto createAccount(AccountDto accountDto) {
        log.info("Создание счета: {}", accountDto);

        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Account> existingAccount = accountRepository.findByAccountNumber(accountDto.getAccountNumber());

        if (!existingAccount.isEmpty()) {
            throw new RuntimeException("Счет с таким номером уже существует");
        }

        Account account = modelMapper.map(accountDto, Account.class);
        account.setUser(user);
        account = accountRepository.save(account);
        log.info("Создан новый счет с ID: {}", account.getId());
        return modelMapper.map(account, AccountDto.class);
    }

    public List<AccountDto> getFilteredAccounts(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance) {
        log.info("Получение счетов с фильтрами: accountNumber = {}, minBalance = {}, maxBalance = {}", accountNumber, minBalance, maxBalance);

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

        if (accounts.isEmpty()) {
            throw new RuntimeException("Не найдено счетов");
        }

        return accounts.stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        log.info("Обновление счета с ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Счет с таким ID не найден"));

        account.setAccountNumber(accountDto.getAccountNumber());
        account.setBalance(accountDto.getBalance());

//        if (accountDto.getDeposits() != null) {
//            account.getDeposits().clear();
//            account.getDeposits().addAll(accountDto.getDeposits().stream()
//                    .map(depositDto -> modelMapper.map(depositDto, Deposit.class))
//                    .collect(Collectors.toList()));
//        }

        account = accountRepository.save(account);
        log.info("Обновлен счет с ID: {}", account.getId());
        return modelMapper.map(account, AccountDto.class);
    }


    @Transactional
    public void deleteAccount(Long id) {
        log.info("Удаление счета с ID: {}", id);

        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Счет с таким ID не найден");
        }

        accountRepository.deleteById(id);
        log.info("Счет с ID: {} удален", id);
    }

    public AccountDto getAccountById(Long id) {
        log.info("Получение счета с ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Счет с таким ID не найден"));
        return modelMapper.map(account, AccountDto.class);
    }
}