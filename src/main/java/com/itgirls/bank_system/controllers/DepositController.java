package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.exception.DepositNotFoundException;
import com.itgirls.bank_system.service.DepositService;
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
public class DepositController {

    private final DepositService depositService;

    @PostMapping()
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
    public List<DepositDto> getAllDeposits() {
        return depositService.getAllDeposits();
    }

    @GetMapping("/{id}")
    public DepositDto getDepositById(@RequestParam long id) {
        return depositService.getDepositById(id);
    }

    @PutMapping()
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
    public String deleteDeposit(@RequestParam long id) {
        return depositService.deleteDeposit(id);
    }

}
