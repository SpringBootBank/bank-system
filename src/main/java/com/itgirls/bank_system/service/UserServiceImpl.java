package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.*;
import com.itgirls.bank_system.enums.TransactionType;
import com.itgirls.bank_system.model.*;
import com.itgirls.bank_system.repository.UserRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto addNewUser(UserCreateDto userCreateDto) {
        String passwordEncode = passwordEncoder.encode(userCreateDto.getPassword());
        User user = new User();
        user.setName(userCreateDto.getName());
        user.setSurname((userCreateDto.getSurname()));
        user.setEmail(userCreateDto.getEmail());
        user.setRole(userCreateDto.getRole());
        user.setPassword(passwordEncode);
        userRepository.save(user);
        return convertUserToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertUserToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto, boolean usePatch) {
        User user = userRepository.findById(userUpdateDto.getId()).
                orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (!usePatch || userUpdateDto.getPassword() != null) {
            String passwordEncode = passwordEncoder.encode(userUpdateDto.getPassword());
            user.setPassword(passwordEncode);
        }
        if (!usePatch || userUpdateDto.getRole() != null) {
            user.setRole(userUpdateDto.getRole());
        }
        if (!usePatch || userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        if (!usePatch || userUpdateDto.getSurname() != null) {
            user.setSurname(userUpdateDto.getSurname());
        }
        if (!usePatch || userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (!usePatch && userUpdateDto.getAccounts() != null) {
            user.setAccounts(userUpdateDto.getAccounts());
        }
        if (!usePatch && userUpdateDto.getDeposits() != null) {
            user.setDeposits(userUpdateDto.getDeposits());
        }
        if (!usePatch && userUpdateDto.getLoans() != null) {
            user.setLoans(userUpdateDto.getLoans());
        }
        if (usePatch && userUpdateDto.getTransactions() != null) {
            user.setTransactions(userUpdateDto.getTransactions());
        }
        userRepository.save(user);
        return convertUserToDto(user);
    }

    @Override
    public UserDto getUserByID(Long id) {
        User foundUser = userRepository.findUserById(id);
        return convertUserToDto(foundUser);
    }

    @Override
    public String deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            try {
                userRepository.deleteById(id);
                return "Пользователь с идентификатором " + id + " успешно удален";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else return "Пользователь с идентификатором " + id + " не найден";
    }

    private UserDto convertUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .accounts(user.getAccounts().stream().map(this::convertAccountToDto).collect(Collectors.toSet()))
                .deposits(user.getDeposits().stream().map(this::convertDepositToDto).collect(Collectors.toSet()))
                .loans(user.getLoans().stream().map(this::convertLoanToDto).collect(Collectors.toSet()))
                .transactions(user.getTransactions().stream().map(this::convertTransactionToDto).collect(Collectors.toSet()))
                .build();
    }


    private AccountDto convertAccountToDto(Account account) {
        AccountDto accountDto = AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .type(account.getType().name())
                .build();
        return accountDto;
    }

    private DepositDto convertDepositToDto(Deposit deposit) {
        return DepositDto.builder()
                .id(deposit.getId())
                .statusDeposit(deposit.getStatusDeposit().name())
                .interestRateDeposit(deposit.getInterestRateDeposit())
                .startDateDeposit(deposit.getStartDateDeposit())
                .endDateDeposit(deposit.getEndDateDeposit())
                .build();
    }

    private LoanDto convertLoanToDto(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .statusLoan(loan.getStatusLoan().name())
                .amountLoan((loan.getAmountLoan()))
                .monthlyPayment(loan.getMonthlyPayment())
                .startDateLoan(loan.getStartDateLoan())
                .endDateLoan(loan.getEndDateLoan())
                .build();
    }

    private TransactionDto convertTransactionToDto(Transactions transactions) {
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
}