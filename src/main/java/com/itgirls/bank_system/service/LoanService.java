package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.LoanDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LoanService {
    LoanDto getLoanById(Long id);

    List<LoanDto> getLoansByStatusAndUserID(String statusLoan, Long userId);

    List<LoanDto> getAllLoans();

    LoanDto createLoan(LoanDto loanDto);

    LoanDto updateLoan(LoanDto loanDto, Authentication authentication);

    public String deleteLoan(Long id);
}