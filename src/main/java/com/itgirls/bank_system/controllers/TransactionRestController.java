package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.TransactionDto;
import com.itgirls.bank_system.service.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
@Tag(name = "транзакции", description = "управление транзакциями")
public class TransactionRestController {
    private final TransactionServiceImpl transactionServiceImpl;

    @PostMapping()
    @Operation(summary = "добавление транзакции", description = "данный метод позволяет создать новую транзакцию")
    public ResponseEntity<?> addNewTransaction(@Valid @RequestBody TransactionDto transactionDto,
                                               BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok().body(transactionServiceImpl.addNewTransaction(transactionDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "изменение транзакции", description = "данный метод позволяет изменить сумму транзакции")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id, @Valid @RequestBody TransactionDto transactionDto,
                                               BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
        transactionDto.setId(id);
        return ResponseEntity.ok().body(transactionServiceImpl.updateAmountOfTransaction(transactionDto));
    }

    @GetMapping()
    @Operation(summary = "просмотр транзакций", description = "данный метод позволяет вывести список всех транзакций в бд")
    public ResponseEntity<?> getAllTransactions() {
        try {
            return ResponseEntity.ok().body(transactionServiceImpl.findAllTransactions());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Транзакции не найдены");
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "просмотр транзакции(ий)", description = "данный метод позволяет администратору просмотреть транзакции" +
            " по id пользователей, а клиенту банка - просмотреть свои транзакции")
    public ResponseEntity<?> getATransactionByBankUserId(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(transactionServiceImpl.findByBankUser_Id(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Транзакции не найдены");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "удаление транзакции", description = "данный метод позволяет удалить транзакцию")
    public String deleteTransaction(@PathVariable("id") Long id) {
        try {
            transactionServiceImpl.deleteTransaction(id);
            return "Транзакция успешно удалена";
        } catch (Exception e) {
            return "Удалить транзакцию невозможно";
        }
    }
}







