package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.exception.DepositNotFoundException;
import com.itgirls.bank_system.model.Deposit;

import java.util.List;

public interface DepositService {
    DepositDto createDeposit(DepositDto depositDto);
    List<DepositDto> getAllDeposits();
    DepositDto getDepositById(Long id);
    Deposit updateDeposit(DepositDto depositDto);
    String deleteDeposit(Long id);
}
