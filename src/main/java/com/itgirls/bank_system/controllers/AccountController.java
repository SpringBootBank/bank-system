package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
@Tag(name = "Счета", description = "Управление счетами")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Добавление нового счёта")
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid AccountDto accountDto) {
        try {
            log.info("Создание нового счета...");
            AccountDto createdAccount = accountService.createAccount(accountDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (Exception e) {
            log.error("Ошибка при создании счета", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Получить всех счетов")
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts(@RequestParam(required = false) String accountNumber,
                                                           @RequestParam(required = false) BigDecimal minBalance,
                                                           @RequestParam(required = false) BigDecimal maxBalance,
                                                           @RequestParam(required = false) String accountType) {
        try {
            log.info("Получение всех счетов с фильтрами...");
            List<AccountDto> accounts = accountService.getFilteredAccounts(accountNumber, minBalance, maxBalance, accountType);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            log.error("Ошибка при получении счетов", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Получить счёта по ID")
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        try {
            log.info("Получение счета с ID: {}", id);
            AccountDto accountDto = accountService.getAccountById(id);
            return ResponseEntity.ok(accountDto);
        } catch (Exception e) {
            log.error("Ошибка при получении счета с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Получить счета по ID пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(@PathVariable Long userId) {
        try {
            log.info("Получение счетов для пользователя с ID: {}", userId);
            List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
            return ResponseEntity.ok(accounts);
        } catch (ResponseStatusException e) {
            log.error("Ошибка при получении счетов для пользователя с ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Изменение счёта по ID")
    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDto accountDto) {
        try {
            log.info("Обновление счета с ID: {}", id);
            AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Ошибка при обновлении счета с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Удаление счёта по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            log.info("Удаление счета с ID: {}", id);
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении счета с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
