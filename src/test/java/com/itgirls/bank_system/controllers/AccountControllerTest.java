package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        accountDto = AccountDto.builder()
                .id(1L)
                .userId(1L)
                .accountNumber("1234567812345678")
                .balance(BigDecimal.valueOf(1000))
                .type("SAVINGS")
                .build();
    }

    @Test
    void createAccountTest() throws Exception {
        when(accountService.createAccount(any(AccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userId\": 1, \"accountNumber\": \"1234567812345678\", \"balance\": 1000, \"type\": \"SAVINGS\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Ваш счёт с номером 1234567812345678 успешно создан"));
    }

    @Test
    void getAllAccountsTest() throws Exception {
        List<AccountDto> accounts = List.of(accountDto);
        when(accountService.getFilteredAccounts(any(), any(), any(), any())).thenReturn(accounts);

        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Найдено 1 счета(ов)"))
                .andExpect(jsonPath("$.accounts[0].id").value(1L))
                .andExpect(jsonPath("$.accounts[0].accountNumber").value("1234567812345678"))
                .andExpect(jsonPath("$.accounts[0].balance").value(1000.00))
                .andExpect(jsonPath("$.accounts[0].type").value("SAVINGS"));
    }

    @Test
    void getAccountByIdTest() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(accountDto);

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accountNumber").value("1234567812345678"));
    }

    @Test
    void createAccountForUserTest() throws Exception {
        when(accountService.createAccountForUser(eq(1L), any(AccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(post("/accounts/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userId\": 1, \"accountNumber\": \"1234567812345678\", \"balance\": 1000, \"type\": \"SAVINGS\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Ваш счёт с номером 1234567812345678 успешно создан для пользователя с ID 1"));
    }

    @Test
    void getAccountsByUserIdTest() throws Exception {
        when(accountService.getAccountsByUserId(1L)).thenReturn(Collections.singletonList(accountDto));

        mockMvc.perform(get("/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Найдено 1 счетов для пользователя с ID 1"));
    }

    @Test
    void updateAccountTest() throws Exception {
        when(accountService.updateAccount(eq(1L), any(AccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userId\": 1, \"accountNumber\": \"1234567812345678\", \"balance\": 2000, \"type\": \"SAVINGS\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Счет с ID 1 успешно обновлен."));
    }

    @Test
    void deleteAccountTest() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Счет с ID 1 успешно удален."));
    }
}