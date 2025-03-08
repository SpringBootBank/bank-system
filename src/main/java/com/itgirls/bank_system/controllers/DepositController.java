package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.exception.DepositNotFoundException;
import com.itgirls.bank_system.exception.FailedConvertToDtoException;
import com.itgirls.bank_system.exception.UserNotFoundException;
import com.itgirls.bank_system.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/deposits")
@Tag(name = "Вклады", description = "Управление вкладами")
public class DepositController {

    private final DepositService depositService;

    @PostMapping()
    @Operation(summary = "Создание вклада", description = "Этот метод позволяет создать новый вклад.")
    public ResponseEntity<?> createDeposit(@Valid @RequestBody DepositDto depositDto, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });

            return ResponseEntity.badRequest().body(errors);
        }
        try {
            return ResponseEntity.ok().body(depositService.createDeposit(depositDto, authentication));
        } catch (NoSuchElementException | UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(IllegalArgumentException | IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataAccessException | FailedConvertToDtoException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping()
    @Operation(summary = "Просмотр списка вкладов", description = "Этот метод позволяет выгрузить список всех вкладов")
    public ResponseEntity<?> getAllDeposits() {
        try {
            return ResponseEntity.ok().body(depositService.getAllDeposits());
        } catch (FailedConvertToDtoException e) {
            log.error("Ошибка {} при конвертации объектов в DTO.", e);
            return ResponseEntity.badRequest().body("Не удалось конвертировать все объекты в DTO.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Просмотр вклада по ID", description = "Этот метод позволяет посмотреть информацию по вкладу по "
            + "ID вклада")
    public ResponseEntity<?> getDepositById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(depositService.getDepositById(id));
        } catch (FailedConvertToDtoException | DepositNotFoundException e) {
            log.error("Ошибка {} при конвертации в DTO.", e);
            return ResponseEntity.badRequest().body("Не удалось конвертировать объект в DTO.");
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Просмотр вклада или списка вкладов у пользователя по ID", description = "Этот метод позволяет " +
            "посмотреть информацию по вкладам у конкретного пользователя по ID пользователя")
    public ResponseEntity<?> getDepositsByUserId(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(depositService.getDepositsByUserId(id));
        } catch(UserNotFoundException | RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping()
    @Operation(summary = "Обновление данных о вкладе", description = "Этот метод позволяет обновить информацию о вкладе" +
            " по ID вклада")
    public ResponseEntity<?> updateDeposit(@Valid @RequestBody DepositDto depositDto, BindingResult result,
                                           Authentication authentication) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });

            return ResponseEntity.badRequest().body(errors);
        }
        try {
            return ResponseEntity.ok().body(depositService.updateDeposit(depositDto, authentication));
        }  catch(FailedConvertToDtoException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(DataAccessException | DepositNotFoundException | UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление вклада", description = "Этот метод позволяет удалить вклад из базы данных по ID вклада")
    public ResponseEntity <String> deleteDeposit(@PathVariable long id, Authentication authentication) {
        try {
            return ResponseEntity.ok().body(depositService.deleteDeposit(id, authentication));
        } catch (Exception e) {
            log.error("Не удалось удалить вклад по ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}