package com.itgirls.bank_system.controllers;


import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.LoanRepository;
import com.itgirls.bank_system.repository.UserRepository;
import com.itgirls.bank_system.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
@Slf4j
@Tag(name = "Кредиты", description = "Управление кредитами")
public class LoanRestController {
    private final LoanService loanService;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @GetMapping
    @Operation(summary = "Просмотр всех кредитов.")
    public ResponseEntity<?> getAllLoans() {
        log.info("Запрос на получение всех кредитов.");
        List<LoanDto> loans = loanService.getAllLoans();
        if (loans.isEmpty()) {
            log.warn("Кредиты не найдены.");
            return ResponseEntity.noContent().build();
        }
        log.info("Найдено {} кредитов.", loans.size());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск кредита по id.")
    public ResponseEntity<?> getLoanById(@PathVariable("id") Long id) {
        log.info("Запрос на получение кредита с id {}.", id);
        LoanDto loanDto = loanService.getLoanById(id);
        if (loanDto == null) {
            String errorMessage = "Кредит с id " + id + " не найден.";
            log.warn(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        log.info("Кредит найден: {}.", loanDto);
        return ResponseEntity.ok(loanDto);
    }

    @GetMapping("/filter")
    @Operation(summary = "Поиск кредитов по статусу и клиенту.")
    public ResponseEntity<?> getLoansByStatusAndUserId(@RequestParam(required = false) String statusLoan,
                                                       @RequestParam(required = false) Long userId) {
        if (userId == null) {
            String errorMessage = "Параметр userId обязателен.";
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = "Пользователь с id " + userId + " не найден.";
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);  // Ответ с 404 статусом
        }
        if (statusLoan == null) {
            String errorMessage = "Параметр statusLoan обязателен.";
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);  // Возвращаем 400 с сообщением об ошибке
        }
        if (statusLoan != null && !statusLoan.matches("^(ACTIVE|CLOSED|OVERDUE)$")) {
            String errorMessage = "Статус кредита должен быть ACTIVE, CLOSED или OVERDUE.";
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        log.info("Кредиты со статусом {} у клиента {}.", statusLoan, userId);
        List<LoanDto> loans = loanService.getLoansByStatusAndUserID(statusLoan, userId);
        if (loans.isEmpty()) {
            log.warn("У клиента {} нет кредитов со статусом {}.", userId, statusLoan);
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(loans);
    }

    @PostMapping
    @Operation(summary = "Создание нового кредита.")
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanDto loanDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Ошибка валидации.");
        }
        LoanDto createdLoan = loanService.createLoan(loanDto);
        return ResponseEntity.ok(createdLoan);
    }

    @PutMapping
    @Operation(summary = "Обновление данных по кредиту.")
    public ResponseEntity<?> updateLoan(@Valid @RequestBody LoanDto loanDto, BindingResult result,
                                        Authentication authentication) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Ошибка валидации.");
        }
        LoanDto updatedLoan = loanService.updateLoan(loanDto, authentication);
        return ResponseEntity.ok(updatedLoan);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление кредита.")
    public ResponseEntity<?> deleteLoan(@PathVariable("id") Long id) {
        log.info("Запрос на удаление кредита с id {}.", id);
        if (!loanRepository.existsById(id)) {
            String errorMessage = "Кредит с id " + id + " не найден.";
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        String responseMessage = loanService.deleteLoan(id);
        log.info("Кредит с id {} успешно удален.", id);
        return ResponseEntity.ok(responseMessage);
    }
}