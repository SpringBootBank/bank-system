package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.TransactionDto;
import com.itgirls.bank_system.exception.FailedConvertToDtoException;
import com.itgirls.bank_system.exception.UserNotFoundException;
import com.itgirls.bank_system.model.Transactions;
import com.itgirls.bank_system.repository.TransactionRepository;
import com.itgirls.bank_system.service.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.itgirls.bank_system.mapper.EntityToDtoMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/transactions")
@Tag(name = "транзакции", description = "управление транзакциями")
public class TransactionRestController {
    private final TransactionServiceImpl transactionServiceImpl;
    private final TransactionRepository transactionRepository;

    @PostMapping()
    @Operation(summary = "добавление транзакции", description = "данный метод позволяет создать новую транзакцию")
    public ResponseEntity<?> addNewTransaction(@Valid @RequestBody TransactionDto transactionDto,
                                               BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            return ResponseEntity.ok().body(transactionServiceImpl.addNewTransaction(transactionDto));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long id) {
        if (!transactionRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("транзакция с id " + id + " отсутствует в бд");
        }
        try {
            transactionServiceImpl.deleteTransaction(id);
            return ResponseEntity.ok().body ("Транзакция с id " + id + " успешно удалена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    public List<TransactionDto> getAllTransactions(@RequestParam(required = false) String type,
                                                   @RequestParam(required = false) Long senderAccountId,
                                                   @RequestParam(required = false) Long beneficiaryAccountId) {
        log.info("Фильтрация: type={}, senderAccountId={}, beneficiaryAccountId={}", type, senderAccountId, beneficiaryAccountId);
        if (StringUtils.isEmpty(type) && senderAccountId == null && beneficiaryAccountId == null) {
            return transactionServiceImpl.findAllTransactions();
        } else {
            List<Transactions> transaction = transactionServiceImpl.getTransactionByTypeOrSenderOrBeneficiary(type,
                    senderAccountId, beneficiaryAccountId);
            log.info("Найдено {} транзакций перед конвертацией", transaction.size());
            return EntityToDtoMapper.convertTransactionToDto(transaction);

        }
    }
}
