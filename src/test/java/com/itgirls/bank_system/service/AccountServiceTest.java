package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.enums.AccountType;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void testCreateAccount() {
        AccountDto accountDto = new AccountDto(8L, "6666666666666666", new BigDecimal(1000), "SAVINGS");
        Account account = new Account(8L, "6666666666666666", new BigDecimal(1000), AccountType.SAVINGS, null, null, null, null, null);

        when(modelMapper.map(accountDto, Account.class)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(modelMapper.map(account, AccountDto.class)).thenReturn(accountDto);

        AccountDto result = accountService.createAccount(accountDto);

        assertEquals(result.getAccountNumber(), "6666666666666666");
        assertEquals(result.getBalance(), new BigDecimal(1000));
    }

    @Test
    public void testGetAccountById() {
        Account account = new Account(8L, "6666666666666666", new BigDecimal(1000), AccountType.SAVINGS, null, null, null, null, null);
        AccountDto accountDto = new AccountDto(8L, "6666666666666666", new BigDecimal(1000), "SAVINGS");

        when(accountRepository.findById(8L)).thenReturn(Optional.of(account));
        when(modelMapper.map(account, AccountDto.class)).thenReturn(accountDto);
        AccountDto result = accountService.getAccountById(8L);
        assertEquals(result.getAccountNumber(), "6666666666666666");
    }

    @Test
    public void testGetAccountByIdThrowsException() {
        when(accountRepository.findById(8L)).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            accountService.getAccountById(8L);
        });
        assertEquals(thrown.getMessage(), "Account not found");
    }
}
