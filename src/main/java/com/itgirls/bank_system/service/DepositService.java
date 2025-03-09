package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.exception.DepositNotFoundException;
import com.itgirls.bank_system.exception.FailedConvertToDtoException;
import com.itgirls.bank_system.exception.UserNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DepositService {
    DepositDto createDeposit(DepositDto depositDto, Authentication authentication)
            throws DataAccessException, FailedConvertToDtoException, UserNotFoundException;

    List<DepositDto> getAllDeposits() throws FailedConvertToDtoException;

    DepositDto getDepositById(Long id) throws FailedConvertToDtoException, DepositNotFoundException;

    List<DepositDto> getDepositsByUserId(Long id) throws UserNotFoundException;

    @Transactional
    DepositDto updateDeposit(DepositDto depositDto, Authentication authentication)
            throws DataAccessException, DepositNotFoundException, UserNotFoundException, FailedConvertToDtoException;

    String deleteDeposit(Long id, Authentication authentication) throws DepositNotFoundException, UserNotFoundException;
}