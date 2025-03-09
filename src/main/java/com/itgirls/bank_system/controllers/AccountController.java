package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Map;

@Controller
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
@Tag(name = "Счета", description = "Управление счетами")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Добавление нового счёта")
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountDto accountDto) {
        try {
            log.info("Создание нового счета...");
            AccountDto createdAccount = accountService.createAccount(accountDto);

            String successMessage = "Ваш счёт с номером " + createdAccount.getAccountNumber() + " успешно создан";

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", successMessage));
        } catch (ResponseStatusException e) {
            log.error("Ошибка при создании счета: {}", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании счета", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @Operation(summary = "Получить всех счетов")
    @GetMapping
    public ResponseEntity<?> getAllAccounts(@RequestParam(required = false) String accountNumber,
                                            @RequestParam(required = false) BigDecimal minBalance,
                                            @RequestParam(required = false) BigDecimal maxBalance,
                                            @RequestParam(required = false) String accountType) {
        try {
            log.info("Получение всех счетов с фильтрами...");
            List<AccountDto> accounts = accountService.getFilteredAccounts(accountNumber, minBalance, maxBalance, accountType);

            String successMessage = "Найдено " + accounts.size() + " счета(ов)";

            return ResponseEntity.ok(Map.of("message", successMessage, "accounts", accounts));
        } catch (ResponseStatusException e) {
            log.error("Ошибка при получении счетов с фильтрами: {}", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении счетов с фильтрами", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @Operation(summary = "Получить счёта по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAccountById(@PathVariable Long id) {
        try {
            log.info("Получение счета с ID: {}", id);
            AccountDto accountDto = accountService.getAccountById(id);

            String successMessage = "Найден счет с ID: "+ id;
            log.info(successMessage);

            return ResponseEntity.ok(Map.of(
                    "message", successMessage,
                    "data", accountDto
            ));
        } catch (EntityNotFoundException e) {
            log.error("Счет с ID {} не найден", id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Счет с ID " + id + " не найден"));
        } catch (Exception e) {
            log.error("Ошибка при получении счета с ID: {}", id, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @Operation(summary = "Добавление нового счёта для пользователя")
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAccountForUser(@PathVariable Long userId, @RequestBody @Valid AccountDto accountDto) {
        try {
            log.info("Создание нового счета для пользователя с ID: {}", userId);
            AccountDto createdAccount = accountService.createAccountForUser(userId, accountDto);

            String successMessage = "Ваш счёт с номером " + createdAccount.getAccountNumber() + " успешно создан для пользователя с ID " + userId;

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", successMessage));
        } catch (ResponseStatusException e) {
            log.error("Ошибка при создании счета для пользователя с ID: {}", userId, e);
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании счета для пользователя с ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @Operation(summary = "Получить счета по ID пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountsByUserId(@PathVariable Long userId) {
        try {
            log.info("Получение счетов для пользователя с ID: {}", userId);
            List<AccountDto> accounts = accountService.getAccountsByUserId(userId);

            String successMessage = "Найдено " + accounts.size() + " счетов для пользователя с ID " + userId;

            return ResponseEntity.ok(Map.of("message", successMessage, "accounts", accounts));
        } catch (ResponseStatusException e) {
            log.error("Ошибка при получении счетов для пользователя с ID: {}", userId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении счетов для пользователя с ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @Operation(summary = "Изменение счёта по ID")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDto accountDto) {
        try {
            log.info("Обновление счета с ID: {}", id);
            AccountDto updatedAccount = accountService.updateAccount(id, accountDto);

            String successMessage = "Счет с ID " + id + " успешно обновлен.";

            return ResponseEntity.ok(Map.of("message", successMessage, "updatedAccount", updatedAccount));
        } catch (ResponseStatusException e) {
            log.error("Ошибка при обновлении счета с ID: {}", id, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении счета с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @Operation(summary = "Удаление счёта по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            log.info("Удаление счета с ID: {}", id);
            accountService.deleteAccount(id);

            String successMessage = "Счет с ID " + id + " успешно удален.";
            return ResponseEntity.ok(Map.of(
                    "message", successMessage
            ));

        } catch (ResponseStatusException e) {
            log.error("Ошибка при удалении счета с ID: {}", id, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при удалении счета с ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }
}
