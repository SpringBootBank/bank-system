package com.itgirls.bank_system.mapper;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityToDtoMapper {

    public static DepositDto convertDepositToDto(Deposit deposit) {
        log.debug("Трансформация вклада с ID {} в DTO.", deposit.getId());
        return DepositDto.builder()
                .id(deposit.getId())
                .account(convertAccountToDto(deposit.getAccount()))
                .user(convertUserToDto(deposit.getUser()))
                .amountDeposit(deposit.getAmountDeposit())
                .startDateDeposit(deposit.getStartDateDeposit())
                .endDateDeposit(deposit.getEndDateDeposit())
                .interestRateDeposit(deposit.getInterestRateDeposit())
                .statusDeposit(String.valueOf(deposit.getStatusDeposit()))
                .build();
    }

    public static UserDto convertUserToDto(User user) {
        log.debug("Трансформация пользователя с ID {} в DTO.", user.getId());
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }

    public static AccountDto convertAccountToDto(Account account) {
        log.debug("Трансформация счета с ID {} в DTO.", account.getId());
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .build();
    }
}
