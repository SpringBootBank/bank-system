package com.itgirls.bank_system.controllers;


import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
@Slf4j
@Tag(name = "Кредиты", description = "Управление кредитами")
public class LoanRestController {
    private final LoanService loanService;

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
        log.info("Кредит найден: {}.", loanDto);
        return ResponseEntity.ok(loanDto);
    }

    @GetMapping("/filter")
    @Operation(summary = "Поиск кредитов по статусу и клиенту.")
    public ResponseEntity<?> getLoansByStatusAndUserId(@RequestParam(required = false) String statusLoan,
                                                       @RequestParam(required = false) Long userId) {
        log.info("Кредиты по статусу {} и клиенту {}.", statusLoan, userId);
        List<LoanDto> loans = loanService.getLoansByStatusAndUserID(statusLoan, userId);
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
        String responseMessage = loanService.deleteLoan(id);
        log.info("Кредит с id {} успешно удален.", id);
        return ResponseEntity.ok(responseMessage);
    }
}