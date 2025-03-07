package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.exception.FailedConvertToDtoException;
import com.itgirls.bank_system.exception.UserNotFoundException;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface DepositService {
    DepositDto createDeposit(DepositDto depositDto) throws DataAccessException, FailedConvertToDtoException;

    List<DepositDto> getAllDeposits() throws FailedConvertToDtoException;

    DepositDto getDepositById(Long id) throws FailedConvertToDtoException;

    List<DepositDto> getDepositsByUserId(Long id) throws UserNotFoundException;

    DepositDto updateDeposit(DepositDto depositDto) throws DataAccessException, FailedConvertToDtoException;

    String deleteDeposit(Long id) throws Exception;
}