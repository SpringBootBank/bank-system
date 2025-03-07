package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.LoanDto;

import java.util.List;

public interface LoanService {
    LoanDto getLoanById(Long id);

    List<LoanDto> getLoansByStatus(String statusLoan);

    List<LoanDto> getAllLoans();

    LoanDto createLoan(LoanDto loanDto);

    LoanDto updateLoan(LoanDto loanDto);

    public String deleteLoan(Long id);
}