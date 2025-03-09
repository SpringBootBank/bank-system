package com.itgirls.bank_system.mapper;

import com.itgirls.bank_system.dto.*;
import com.itgirls.bank_system.model.*;
import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.Loan;
import com.itgirls.bank_system.model.User;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

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

    public static UserDto convertNewUserToDto(User user) {
        log.debug("Трансформация созданного пользователя с ID {} в DTO", user.getId());
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }

    public static UserDto convertEveryUserToDto(User user) {
        log.debug("Трансформация пользователей в DTO");
        if (user == null) {
            log.error("Пользователь с пустыми полями не может быть создан");
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .accounts(user.getAccounts().stream().map(EntityToDtoMapper::convertAccountsToDto).collect(Collectors.toSet()))
                .deposits(user.getDeposits().stream().map(EntityToDtoMapper::convertDepositsToDto).collect(Collectors.toSet()))
                .loans(user.getLoans().stream().map(EntityToDtoMapper::convertLoansToDto).collect(Collectors.toSet()))
                .transactions(user.getTransactions().stream().map(EntityToDtoMapper::convertTransactionsToDto).collect(Collectors.toSet()))
                .build();
    }

    public static AccountDto convertAccountsToDto(Account account) {
        log.debug("Трансформация счета с ID {} в DTO", account.getId());
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .type(account.getType().name())
                .build();
    }

    private static DepositDto convertDepositsToDto(Deposit deposit) {
        log.debug("Трансформация депозита с ID {} в DTO", deposit.getId());
        return DepositDto.builder()
                .id(deposit.getId())
                .statusDeposit(deposit.getStatusDeposit().name())
                .interestRateDeposit(deposit.getInterestRateDeposit())
                .startDateDeposit(deposit.getStartDateDeposit())
                .endDateDeposit(deposit.getEndDateDeposit())
                .build();
    }

    private static LoanDto convertLoansToDto(Loan loan) {
        log.debug("Трансформация кредита с ID {} в DTO", loan.getId());
        return LoanDto.builder()
                .id(loan.getId())
                .statusLoan(loan.getStatusLoan().name())
                .amountLoan((loan.getAmountLoan()))
                .monthlyPayment(loan.getMonthlyPayment())
                .startDateLoan(loan.getStartDateLoan())
                .endDateLoan(loan.getEndDateLoan())
                .build();
    }

    private static TransactionDto convertTransactionsToDto(Transactions transactions) {
        log.debug("Трансформация транзакции с ID {} в DTO", transactions.getId());
        return TransactionDto.builder()
                .id(transactions.getId())
                .transactionNumber(transactions.getTransactionNumber())
                .transactionType(transactions.getTransactionType().name())
                .transactionAmount(transactions.getTransactionAmount())
                .transactionTime(transactions.getTransactionTime())
                .senderAccountId(transactions.getSenderAccount().getId())
                .beneficiaryAccountId(transactions.getBeneficiaryAccount().getId())
                .build();
    }

    public static LoanDto convertLoanEntityToDto(Loan loan) {
        log.debug("Трансформация кредито с ID {} в DTO.", loan.getId());
        return LoanDto.builder()
                .id(loan.getId())
                .amountLoan(loan.getAmountLoan())
                .interestRateLoan(loan.getInterestRateLoan())
                .startDateLoan(loan.getStartDateLoan())
                .endDateLoan(loan.getEndDateLoan())
                .monthlyPayment(loan.getMonthlyPayment())
                .statusLoan(loan.getStatusLoan().name())
                .accountId(loan.getAccount().getId())
                .accountNumber(loan.getAccount().getAccountNumber())
                .userId(loan.getUser().getId())
                .userName(loan.getUser().getName())
                .userSurname(loan.getUser().getSurname())
                .build();
    }
}
