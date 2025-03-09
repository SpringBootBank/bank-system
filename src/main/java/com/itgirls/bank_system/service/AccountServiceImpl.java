package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.enums.AccountType;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.UserRepository;
import com.itgirls.bank_system.specification.AccountSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public AccountDto createAccount(AccountDto accountDto) {
        log.info("Создание счета: {}", accountDto);

        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким ID не найден"));

        if (accountDto.getAccountNumber() == null || !accountDto.getAccountNumber().matches("\\d{16}")) {
            log.error("Неверный формат номера счета: {}", accountDto.getAccountNumber());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Номер счета должен содержать 16 цифр");
        }

        if (accountDto.getBalance() == null || accountDto.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Неверный баланс счета: {}", accountDto.getBalance());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Баланс счета должен быть положительным числом");
        }

        if (!accountRepository.findByAccountNumber(accountDto.getAccountNumber()).isEmpty()) {
            log.error("Счет с таким номером уже существует: {}", accountDto.getAccountNumber());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Счет с таким номером уже существует");
        }

        if (accountDto.getType() != null) {
            validateAccountType(accountDto.getType());
        }

        Account account = modelMapper.map(accountDto, Account.class);
        account.setUser(user);
        account.setType(AccountType.valueOf(accountDto.getType().toUpperCase()));
        account = accountRepository.save(account);

        log.info("Создан новый счет с ID: {}", account.getId());
        return modelMapper.map(account, AccountDto.class);
    }

    private void validateAccountType(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип счета не может быть пустым");
        }

        try {
            AccountType.valueOf(accountType.toUpperCase());
        } catch (IllegalArgumentException e) {
            String validTypes = Arrays.stream(AccountType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Неверный тип счета. Доступные типы: " + validTypes);
        }
    }

    @Transactional
    @Override
    public List<AccountDto> getFilteredAccounts(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance, String accountType) {
        log.info("Получение счетов с фильтрами: accountNumber = {}, minBalance = {}, maxBalance = {}, accountType = {}", accountNumber, minBalance, maxBalance, accountType);

        if (accountNumber != null && !accountNumber.matches("\\d{16}")) {
            log.error("Неверный формат номера счета: {}", accountNumber);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Номер счета должен содержать 16 цифр");
        }

        if (minBalance != null && minBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Неверное значение минимального баланса: {}", minBalance);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Минимальный баланс не может быть отрицательным");
        }
        if (maxBalance != null && maxBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Неверное значение максимального баланса: {}", maxBalance);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Максимальный баланс не может быть отрицательным");
        }
        if (minBalance != null && maxBalance != null && minBalance.compareTo(maxBalance) > 0) {
            log.error("Минимальный баланс больше максимального: minBalance = {}, maxBalance = {}", minBalance, maxBalance);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Минимальный баланс не может быть больше максимального");
        }

        if (accountType != null) {
            validateAccountType(accountType);
        }

        Specification<Account> spec = new AccountSpecification(accountNumber, minBalance, maxBalance, accountType);

        List<Account> accounts = accountRepository.findAll(spec);
        if (accounts.isEmpty()) {
            log.warn("Не найдено счетов по заданным фильтрам");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдено счетов по заданным параметрам");
        }

        return accounts.stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        log.info("Обновление счета с ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счет с таким ID не найден"));

        if (accountDto.getAccountNumber() != null) {
            if (!accountDto.getAccountNumber().matches("\\d{16}")) {
                log.error("Неверный номер счета: {}", accountDto.getAccountNumber());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Номер счета должен содержать 16 цифр");
            }
            account.setAccountNumber(accountDto.getAccountNumber());
        }

        if (accountDto.getBalance() != null) {
            if (accountDto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                log.error("Неверная сумма баланса: {}", accountDto.getBalance());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Баланс должен быть положительным числом");
            }
            account.setBalance(accountDto.getBalance());
        }

        if (accountDto.getType() != null) {
            validateAccountType(accountDto.getType());
            account.setType(AccountType.valueOf(accountDto.getType().toUpperCase()));
        }

        account = accountRepository.save(account);
        log.info("Обновлен счет с ID: {}", account.getId());

        return modelMapper.map(account, AccountDto.class);
    }

    @Transactional
    @Override
    public void deleteAccount(Long id) {
        log.info("Удаление счета с ID: {}", id);

        if (!accountRepository.existsById(id)) {
            log.error("Счет с таким ID: {} не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Счет с таким ID не найден");
        }

        accountRepository.deleteById(id);
        log.info("Счет с ID: {} удален", id);
    }

    @Transactional
    @Override
    public AccountDto getAccountById(Long id) {
        log.info("Получение счета с ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счет с таким ID не найден"));
        return modelMapper.map(account, AccountDto.class);
    }

    @Override
    @Transactional
    public List<AccountDto> getAccountsByUserId(Long userId) {
        log.info("Получение счетов для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким ID не найден"));

        List<Account> accounts = accountRepository.findByUser(user);

        if (accounts.isEmpty()) {
            log.warn("Не найдено счета(ов) для пользователя с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдено счета(ов) для пользователя с таким ID");
        }

        return accounts.stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto createAccountForUser(Long userId, AccountDto accountDto) {
        log.info("Создание счета для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким ID не найден"));

        if (accountDto.getAccountNumber() == null || !accountDto.getAccountNumber().matches("\\d{16}")) {
            log.error("Неверный формат номера счета: {}", accountDto.getAccountNumber());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Номер счета должен содержать 16 цифр");
        }

        if (accountDto.getBalance() == null || accountDto.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Неверный баланс счета: {}", accountDto.getBalance());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Баланс счета должен быть положительным числом");
        }

        if (!accountRepository.findByAccountNumber(accountDto.getAccountNumber()).isEmpty()) {
            log.error("Счет с таким номером уже существует: {}", accountDto.getAccountNumber());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Счет с таким номером уже существует");
        }

        if (accountDto.getType() != null) {
            validateAccountType(accountDto.getType());
        }

        Account account = modelMapper.map(accountDto, Account.class);
        account.setUser(user);
        account.setType(AccountType.valueOf(accountDto.getType().toUpperCase()));
        account = accountRepository.save(account);

        log.info("Создан новый счет с ID: {}", account.getId());
        return modelMapper.map(account, AccountDto.class);
    }
}
