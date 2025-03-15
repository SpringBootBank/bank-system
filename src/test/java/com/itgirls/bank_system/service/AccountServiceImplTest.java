package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.enums.AccountType;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountServiceImpl(accountRepository, modelMapper, userRepository);

        account = new Account();
        account.setId(1L);
        account.setAccountNumber("6666666666666666");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setType(AccountType.valueOf("SAVINGS"));

        accountDto = new AccountDto();
        accountDto.setUserId(50L);
        accountDto.setAccountNumber("6666666666666666");
        accountDto.setBalance(BigDecimal.valueOf(1000));
        accountDto.setType("SAVINGS");

        when(modelMapper.map(any(Account.class), eq(AccountDto.class))).thenReturn(accountDto);
    }

    @Test
    void getAccountByIdTest() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(account.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void getAccountById_NotFoundTest() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accountService.getAccountById(1L));

        assertEquals("Счет с таким ID не найден", exception.getMessage());
    }

    @Test
    void updateAccountTest() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.updateAccount(1L, accountDto);

        assertNotNull(result);
        assertEquals(accountDto.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void updateAccount_NotFoundTest() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> accountService.updateAccount(1L, accountDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Счет с таким ID не найден", exception.getReason());
    }

    @Test
    void deleteAccountTest() {
        when(accountRepository.existsById(1L)).thenReturn(true);
        doNothing().when(accountRepository).deleteById(1L);

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAccount_NotFoundTest() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> accountService.deleteAccount(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Счет с таким ID не найден", exception.getReason());
    }
}