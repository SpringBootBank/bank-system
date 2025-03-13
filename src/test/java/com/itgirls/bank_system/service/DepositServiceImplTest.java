package com.itgirls.bank_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.enums.AccountType;
import com.itgirls.bank_system.enums.DepositStatus;
import com.itgirls.bank_system.exception.DepositNotFoundException;
import com.itgirls.bank_system.exception.FailedConvertToDtoException;
import com.itgirls.bank_system.exception.UserNotFoundException;
import com.itgirls.bank_system.model.*;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.DepositRepository;
import com.itgirls.bank_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceImplTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DepositServiceImpl depositService;

    private Deposit deposit;
    private DepositDto depositDto;
    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(3L);

        Set<Loan> loans = new HashSet<>();
        Set<Transactions> outgoingTransactions = new HashSet<>();
        Set<Transactions> incomingTransactions = new HashSet<>();
        account = new Account(1L, "1234567890123456", BigDecimal.valueOf(10000.00),
                AccountType.SAVINGS, user, loans, deposit, outgoingTransactions, incomingTransactions);

        deposit = new Deposit(2L, BigDecimal.valueOf(100.00), BigDecimal.valueOf(5.5),
                LocalDate.parse("2025-03-15"), LocalDate.parse("2027-03-15"), DepositStatus.ACTIVE, account, user);

        ObjectMapper objectMapper = new ObjectMapper();
        depositDto = new DepositDto();
        depositDto.setId(deposit.getId());
        depositDto.setAmountDeposit(deposit.getAmountDeposit());
        depositDto.setInterestRateDeposit(deposit.getInterestRateDeposit());
        depositDto.setStartDateDeposit(deposit.getStartDateDeposit());
        depositDto.setEndDateDeposit(deposit.getEndDateDeposit());
        depositDto.setStatusDeposit(String.valueOf(deposit.getStatusDeposit()));
        depositDto.setAccount(objectMapper.convertValue(deposit.getAccount(), AccountDto.class));
        depositDto.setUser(objectMapper.convertValue(deposit.getUser(), UserDto.class));

        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
    }

    @Test
    void createDeposit_success() throws UserNotFoundException, FailedConvertToDtoException {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        Mockito.when(depositRepository.save(Mockito.any(Deposit.class))).thenReturn(deposit);

        DepositDto result = depositService.createDeposit(depositDto, authentication);

        assertNotNull(result);
        assertEquals(100.00, result.getAmountDeposit());
        assertEquals(5.5, result.getInterestRateDeposit());
        assertEquals("2025-03-15", result.getStartDateDeposit());
        assertEquals("2027-03-15", result.getEndDateDeposit());
        assertEquals("ACTIVE", result.getStatusDeposit());
        assertEquals(1L, result.getAccount());

    }

    @Test
    void createDeposit_accountNotFound() {
        Mockito.when(accountRepository.findById(account.getId())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> depositService.createDeposit(depositDto, authentication));
    }

    @Test
    void createDeposit_userNotFound() {
        Mockito.when(userRepository.findUserByEmail(authentication.getName())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> depositService.createDeposit(depositDto, authentication));
    }

    @Test
    void createDeposit_accountNotMatchingUser() {
        User otherUser = new User();
        otherUser.setId(4L);
        account.setUser(otherUser);

        Mockito.when(accountRepository.findById(account.getId())).thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class, () -> depositService.createDeposit(depositDto, authentication));
    }

    @Test
    void createDeposit_accountHasDeposit() {
        account.setDeposit(deposit);

        Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        Mockito.when(userRepository.findUserByEmail(authentication.getName())).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> depositService.createDeposit(depositDto, authentication));
    }

    @Test
    void getAllDeposits() throws FailedConvertToDtoException {
        Mockito.when(depositRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(deposit));

        List<DepositDto> depositDtoList = depositService.getAllDeposits();

        assertEquals(1, depositDtoList.size());
        assertEquals(2L, depositDtoList.getFirst().getId());
    }

    @Test
    void getDepositById_success() throws FailedConvertToDtoException, DepositNotFoundException {
        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));

        DepositDto result = depositService.getDepositById(deposit.getId());

        assertEquals(2L, result.getId());
    }

    @Test
    void getDepositById_unSuccess() {
        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(DepositNotFoundException.class, () -> depositService.getDepositById(2L));
    }

    @Test
    void getDepositsByUserId() throws UserNotFoundException {
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        List<DepositDto> depositDtoList = depositService.getDepositsByUserId(user.getId());

        assertEquals(1, depositDtoList.size());
        assertEquals(depositDto, depositDtoList.getFirst());
    }

    @Test
    void getDepositsByUserId_unSuccess() {
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> depositService.getDepositsByUserId(3L));
    }

    @Test
    void updateDeposit() throws UserNotFoundException, FailedConvertToDtoException, DepositNotFoundException {
        deposit.setStatusDeposit(DepositStatus.FROZEN);

        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));
        Mockito.when(depositRepository.save(Mockito.any(Deposit.class))).thenReturn(deposit);

        DepositDto result = depositService.updateDeposit(depositDto, authentication);

        assertNotNull(result);
        assertEquals("FROZEN", result.getStatusDeposit());
    }

    @Test
    void updateDeposit_depositNotFound() {
        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(DepositNotFoundException.class, () -> depositService.updateDeposit(depositDto, authentication));
    }

    @Test
    void updateDeposit_userNotFound() {
        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));
        Mockito.when(userRepository.findUserByEmail("test@gmail.com")).thenThrow(new NoSuchElementException());

        assertThrows(UserNotFoundException.class, () -> depositService.updateDeposit(depositDto, authentication));
    }

    @Test
    void updateDeposit_unauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(4L);
        deposit.setUser(otherUser);

        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));
        Mockito.when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> depositService.updateDeposit(depositDto, authentication));
    }

    @Test
    void deleteDeposit_success() throws UserNotFoundException, DepositNotFoundException {
        long id = deposit.getId();
        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));
        Mockito.doNothing().when(depositRepository).deleteById(id);

        String result = depositService.deleteDeposit(2L, authentication);

        assertEquals("Вклад с ID " + id + " был удален.", result);
    }

    @Test
    void deleteDeposit_depositNotFound() {
        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(DepositNotFoundException.class, () -> depositService.deleteDeposit(2L, authentication));
    }

    @Test
    void deleteDeposit_unauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(4L);
        deposit.setUser(otherUser);

        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));
        Mockito.when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> depositService.deleteDeposit(2L, authentication));
    }

    @Test
    void deleteDeposit_unSuccess() {
        long id = deposit.getId();

        Mockito.when(depositRepository.findById(2L)).thenReturn(Optional.of(deposit));
        Mockito.when(depositRepository.count()).thenReturn(1L);
        Mockito.doNothing().when(depositRepository).deleteById(id);
        Mockito.doNothing().when(depositRepository).flush();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> depositService.deleteDeposit(id, authentication));

        assertEquals("Не удалось удалить вклад с ID " + id + " из БД.", exception.getMessage());
    }
}