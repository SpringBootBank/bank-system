package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deposits")
@Tag(name = "Вклады", description = "Управление вкладами")
public class DepositController {

    private final DepositService depositService;

    @PostMapping()
    @Operation(summary = "Создание вклада", description = "Этот метод позволяет создать новый вклад.")
    public ResponseEntity<?> createDeposit(@Valid @RequestBody DepositDto depositDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });

            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().body(depositService.createDeposit(depositDto));
    }

    @GetMapping()
    @Operation(summary = "Просмотр списка вкладов", description = "Этот метод позволяет выгрузить список всех вкладов")
    public List<DepositDto> getAllDeposits() {
        return depositService.getAllDeposits();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Просмотр вклада по ID", description = "Этот метод позволяет посмотреть информацию по вкладу по "
            + "ID вклада")
    public DepositDto getDepositById(@RequestParam long id) {
        return depositService.getDepositById(id);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Просмотр вклада или списка вкладов у пользователя по ID", description = "Этот метод позволяет " +
            "посмотреть информацию по вкладам у конкретного пользователя по ID пользователя")
    public List<DepositDto> getDepositsByUserId(@RequestParam long id) {
        return depositService.getDepositsByUserId(id);
    }

    @PutMapping()
    @Operation(summary = "Обновление данных о вкладе", description = "Этот метод позволяет обновить информацию о вкладе" +
            " по ID вклада")
    public ResponseEntity<?> updateDeposit(@Valid @RequestBody DepositDto depositDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                errors.put(fieldError.getField(), objectError.getDefaultMessage());
            });

            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().body(depositService.updateDeposit(depositDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление вклада", description = "Этот метод позволяет удалить вклад из базы данных по ID вклада")
    public String deleteDeposit(@RequestParam long id) {
        return depositService.deleteDeposit(id);
    }

}
