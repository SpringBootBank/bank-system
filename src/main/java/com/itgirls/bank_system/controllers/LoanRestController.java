package com.itgirls.bank_system.controllers;


import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanRestController {
    private final LoanService loanService;

    @GetMapping("/")
    List<LoanDto> getAllLoans(){
        return loanService.getAllLoans();
    }

    @GetMapping("/{id}")
    LoanDto getLoanById(@PathVariable("id") Long id) {
        return loanService.getLoanById(id);
    }

    @GetMapping
    List<LoanDto> getLoansByStatus(@RequestParam("statusLoan") String statusLoan){
        return loanService.getLoansByStatus(statusLoan);
    }

    @PostMapping()
    LoanDto createLoan(@RequestBody @Valid LoanDto loanDto) {
        return loanService.createLoan(loanDto);
    }

    @PutMapping()
    LoanDto updateLoan(@RequestBody @Valid LoanDto loanDto) {
        return loanService.updateLoan(loanDto);
    }

    @DeleteMapping("/{id}")
    String deleteLoan(@PathVariable("id") Long id){
        return loanService.deleteLoan(id);
    }
}