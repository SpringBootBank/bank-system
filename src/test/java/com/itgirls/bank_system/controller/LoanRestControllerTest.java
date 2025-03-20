package com.itgirls.bank_system.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.LoanRepository;
import com.itgirls.bank_system.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class LoanRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanService loanService;
    @MockitoBean
    private LoanRepository loanRepository;

    @Test
    public void testGetLoanByIdSuccess() throws Exception {
        Long id = 1L;
        LoanDto loanDto = LoanDto.builder()
                .id(id)
                .amountLoan(BigDecimal.valueOf(350.0))
                .interestRateLoan(BigDecimal.valueOf(6.0))
                .startDateLoan(LocalDate.parse("2025-04-01"))
                .endDateLoan(LocalDate.parse("2026-04-01"))
                .monthlyPayment(BigDecimal.valueOf(30.12))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(1L)
                .build();

        when(loanService.getLoanById(id)).thenReturn(loanDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loanDto.getId()))
                .andExpect(jsonPath("$.amountLoan").value(350.0))
                .andExpect(jsonPath("$.interestRateLoan").value(6.0))
                .andExpect(jsonPath("$.startDateLoan").value("2025-04-01"))
                .andExpect(jsonPath("$.endDateLoan").value("2026-04-01"))
                .andExpect(jsonPath("$.monthlyPayment").value(30.12))
                .andExpect(jsonPath("$.statusLoan").value(loanDto.getStatusLoan()))
                .andExpect(jsonPath("$.accountId").value(loanDto.getAccountId()))
                .andExpect(jsonPath("$.userId").value(loanDto.getUserId()));
        verify(loanService, times(1)).getLoanById(id);
    }

    @Test
    public void testGetLoanByIdWhenIdNotFound() throws Exception {
        Long id = 777L;

        when(loanService.getLoanById(id)).thenThrow(new NoSuchElementException("Кредит с id " + id + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Кредит с id " + id + " не найден."));
        verify(loanService, times(1)).getLoanById(id);
    }

    @Test
    public void testGetLoanByIdWhenWrongIdFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/{id}", "АБВ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Некорректный формат id: АБВ"));
        verify(loanService, times(0)).getLoanById(any());
    }

    @Test
    public void testGetLoanByIdDatabaseError() throws Exception {
        Long id = 1L;

        when(loanService.getLoanById(id)).thenThrow(new DataAccessException("Connection failed") {
        });
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error: Connection failed"));
        verify(loanService, times(1)).getLoanById(id);
    }

    @Test
    public void testGetLoansByStatusAndUserIdSuccess() throws Exception {
        String statusLoan = "ACTIVE";
        Long userId = 1L;
        Long accountId = 1L;
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(BigDecimal.valueOf(350.0))
                .interestRateLoan(BigDecimal.valueOf(6.0))
                .startDateLoan(LocalDate.parse("2025-04-01"))
                .endDateLoan(LocalDate.parse("2026-04-01"))
                .monthlyPayment(BigDecimal.valueOf(30.12))
                .statusLoan("ACTIVE")
                .accountId(accountId)
                .userId(userId)
                .build();
        List<LoanDto> loanDtos = List.of(loanDto);

        when(loanService.getLoansByStatusAndUserID(statusLoan, userId)).thenReturn(loanDtos);
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("statusLoan", statusLoan)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(loanDtos.size()))
                .andExpect(jsonPath("$[0].id").value(loanDto.getId()))
                .andExpect(jsonPath("$[0].amountLoan").value(350.0))
                .andExpect(jsonPath("$[0].interestRateLoan").value(6.0))
                .andExpect(jsonPath("$[0].startDateLoan").value("2025-04-01"))
                .andExpect(jsonPath("$[0].endDateLoan").value("2026-04-01"))
                .andExpect(jsonPath("$[0].monthlyPayment").value(30.12))
                .andExpect(jsonPath("$[0].statusLoan").value(loanDto.getStatusLoan()))
                .andExpect(jsonPath("$[0].accountId").value(loanDto.getAccountId()))
                .andExpect(jsonPath("$[0].userId").value(loanDto.getUserId()))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("Кредиты со статусом " + statusLoan + " у клиента " + userId + ": " + responseBody);
                });
        verify(loanService, times(1)).getLoansByStatusAndUserID(statusLoan, userId);
    }

    @Test
    public void testGetLoansByStatusAndUserIdEmptyList() throws Exception {
        String statusLoan = "ACTIVE";
        Long userId = 1L;

        when(loanService.getLoansByStatusAndUserID(statusLoan, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("statusLoan", statusLoan)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(loanService, times(1)).getLoansByStatusAndUserID(statusLoan, userId);
    }

    @Test
    public void testGetLoansByStatusAndUserIdWhenMissingUser() throws Exception {
        String statusLoan = "ACTIVE";

        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("statusLoan", statusLoan))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Параметр userId обязателен."));
        verify(loanService, times(0)).getLoansByStatusAndUserID(anyString(), anyLong());
    }

    @Test
    public void testGetLoansByStatusAndUserIdWhenUserNotFound() throws Exception {
        String statusLoan = "ACTIVE";
        Long userId = 777L;

        when(loanService.getLoansByStatusAndUserID(statusLoan, userId)).thenThrow(new NoSuchElementException("Пользователь с id " + userId + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("statusLoan", statusLoan)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Пользователь с id " + userId + " не найден."));
        verify(loanService, times(0)).getLoansByStatusAndUserID(statusLoan, userId);
    }

    @Test
    public void testGetLoansByStatusAndUserIdWhenMissingStatus() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Параметр statusLoan обязателен."));
        verify(loanService, times(0)).getLoansByStatusAndUserID(anyString(), eq(userId));
    }

    @Test
    public void testGetLoansByStatusAndUserIdWhenWrongStatus() throws Exception {
        String statusLoan = "WRONG";
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("statusLoan", statusLoan)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Статус кредита должен быть ACTIVE, CLOSED или OVERDUE."));
        verify(loanService, times(0)).getLoansByStatusAndUserID(eq(statusLoan), eq(userId));
    }

    @Test
    public void testGetLoansByStatusAndUserIdDatabaseError() throws Exception {
        String statusLoan = "ACTIVE";
        Long userId = 1L;

        when(loanService.getLoansByStatusAndUserID(statusLoan, userId)).thenThrow(new DataAccessException("Connection failed") {
        });
        mockMvc.perform(MockMvcRequestBuilders.get("/loans/filter")
                        .param("statusLoan", statusLoan)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error: Connection failed"));
        verify(loanService, times(1)).getLoansByStatusAndUserID(statusLoan, userId);
    }

    @Test
    public void testGetAllLoansSuccess() throws Exception {
        LoanDto loanDto1 = LoanDto.builder()
                .id(1L)
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-03-10"))
                .endDateLoan(LocalDate.parse("2025-09-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(10L)
                .userId(10L)
                .build();
        LoanDto loanDto2 = LoanDto.builder()
                .id(2L)
                .amountLoan(BigDecimal.valueOf(500.0))
                .interestRateLoan(BigDecimal.valueOf(5.0))
                .startDateLoan(LocalDate.parse("2025-01-20"))
                .endDateLoan(LocalDate.parse("2026-01-20"))
                .monthlyPayment(BigDecimal.valueOf(42.8))
                .statusLoan("ACTIVE")
                .accountId(20L)
                .userId(20L)
                .build();
        List<LoanDto> loanDtos = List.of(loanDto1, loanDto2);

        when(loanService.getAllLoans()).thenReturn(loanDtos);
        mockMvc.perform(MockMvcRequestBuilders.get("/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
        verify(loanService, times(1)).getAllLoans();
    }

    @Test
    public void testGetAllLoansEmptyList() throws Exception {
        when(loanService.getAllLoans()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(loanService, times(1)).getAllLoans();
    }

    @Test
    public void testGetAllLoansDatabaseError() throws Exception {
        when(loanService.getAllLoans()).thenThrow(new DataAccessException("Connection failed") {});

        mockMvc.perform(MockMvcRequestBuilders.get("/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error: Connection failed"));
        verify(loanService, times(1)).getAllLoans();
    }

    @Test
    public void testCreateLoanSuccess() throws Exception {
        LoanDto loanDto = LoanDto.builder()
                .id(6L)
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(1L)
                .build();

        when(loanService.createLoan(loanDto)).thenReturn(loanDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.amountLoan").value(300.0))
                .andExpect(jsonPath("$.interestRateLoan").value(2.0))
                .andExpect(jsonPath("$.startDateLoan").value("2025-02-10"))
                .andExpect(jsonPath("$.endDateLoan").value("2025-08-10"))
                .andExpect(jsonPath("$.monthlyPayment").value(50.29))
                .andExpect(jsonPath("$.statusLoan").value("ACTIVE"))
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("Кредит создан: " + responseBody);
                });
        verify(loanService, times(1)).createLoan(any(LoanDto.class));
    }

    @Test
    public void testCreateLoanWhenAccountNotFound() throws Exception {
        Long accountId = 777L;
        LoanDto loanDto = LoanDto.builder()
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(accountId)
                .userId(1L)
                .build();

        when(loanService.createLoan(any(LoanDto.class))).thenThrow(new NoSuchElementException("Счет с id " + accountId + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Счет с id " + accountId + " не найден."))
                .andReturn();
        verify(loanService, times(1)).createLoan(any(LoanDto.class));
    }

    @Test
    public void testCreateLoanWhenUserNotFound() throws Exception {
        Long userId = 777L;
        LoanDto loanDto = LoanDto.builder()
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(userId)
                .build();

        when(loanService.createLoan(any(LoanDto.class))).thenThrow(new NoSuchElementException("Клиент с id " + userId + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Клиент с id " + userId + " не найден."))
                .andReturn();
        verify(loanService, times(1)).createLoan(any(LoanDto.class));
    }

    @Test
    public void testCreateLoanWhenAccountBelongsToAnotherUser() throws Exception {
        Long accountId = 1L;
        Long userId = 10L;
        LoanDto loanDto = LoanDto.builder()
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(accountId)
                .userId(userId)
                .build();
        Account account = new Account();
        User accountUser = new User();
        accountUser.setId(20L);
        account.setUser(accountUser);
        User user = new User();
        user.setId(userId);

        when(loanService.createLoan(any(LoanDto.class))).thenThrow(new IllegalStateException("Этот счет принадлежит другому клиенту."));
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Этот счет принадлежит другому клиенту."))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println(responseBody);
                })
                .andReturn();
        verify(loanService, times(1)).createLoan(any(LoanDto.class));
    }

    @Test
    public void testCreateLoanEmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println(responseBody);
                });
        verify(loanService, times(0)).createLoan(any(LoanDto.class));
    }

    @Test
    public void testCreateLoanDatabaseError() throws Exception {
        LoanDto loanDto = LoanDto.builder()
                .id(6L)
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(1L)
                .build();

        when(loanService.createLoan(loanDto)).thenThrow(new DataAccessException("Connection failed") {
        });
        mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error: Connection failed"));
        verify(loanService, times(1)).createLoan(loanDto);
    }

    @Test
    public void testUpdateLoanSuccess() throws Exception {
        LoanDto loanDto = LoanDto.builder()
                .id(6L)
                .amountLoan(BigDecimal.valueOf(300.0))
                .interestRateLoan(BigDecimal.valueOf(2.0))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("CLOSED")
                .accountId(1L)
                .userId(1L)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        when(loanService.updateLoan(loanDto, authentication)).thenReturn(loanDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.amountLoan").value(300.0))
                .andExpect(jsonPath("$.interestRateLoan").value(2.0))
                .andExpect(jsonPath("$.startDateLoan").value("2025-02-10"))
                .andExpect(jsonPath("$.endDateLoan").value("2025-08-10"))
                .andExpect(jsonPath("$.monthlyPayment").value(50.29))
                .andExpect(jsonPath("$.statusLoan").value("CLOSED"))
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("Кредит обновлен: " + responseBody);
                });
        verify(loanService, times(1)).updateLoan(loanDto, authentication);
    }

    @Test
    public void testUpdateLoanWhenLoanNotFound() throws Exception {
        Long id = 777L;
        LoanDto loanDto = LoanDto.builder()
                .id(id)
                .amountLoan(BigDecimal.valueOf(500.0))
                .interestRateLoan(BigDecimal.valueOf(3.5))
                .startDateLoan(LocalDate.parse("2025-03-01"))
                .endDateLoan(LocalDate.parse("2026-03-01"))
                .monthlyPayment(BigDecimal.valueOf(100.0))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(10L)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        when(loanService.updateLoan(any(LoanDto.class), any(Authentication.class))).thenThrow(new NoSuchElementException("Кредит с id " + id + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto))
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Кредит с id " + id + " не найден."));
        verify(loanService, times(1)).updateLoan(any(LoanDto.class), any(Authentication.class));
    }

    @Test
    public void testUpdateLoanWhenAccountNotFound() throws Exception {
        Long accountId = 777L;
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(BigDecimal.valueOf(500.0))
                .interestRateLoan(BigDecimal.valueOf(3.5))
                .startDateLoan(LocalDate.parse("2025-03-01"))
                .endDateLoan(LocalDate.parse("2026-03-01"))
                .monthlyPayment(BigDecimal.valueOf(100.0))
                .statusLoan("ACTIVE")
                .accountId(accountId)
                .userId(10L)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        when(loanService.updateLoan(any(LoanDto.class), any(Authentication.class))).thenThrow(new NoSuchElementException("Счет с id " + accountId + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto))
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Счет с id " + accountId + " не найден."));
        verify(loanService, times(1)).updateLoan(any(LoanDto.class), any(Authentication.class));
    }

    @Test
    public void testUpdateLoanWhenUserNotFound() throws Exception {
        Long userId = 777L;
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(BigDecimal.valueOf(500.0))
                .interestRateLoan(BigDecimal.valueOf(3.5))
                .startDateLoan(LocalDate.parse("2025-03-01"))
                .endDateLoan(LocalDate.parse("2026-03-01"))
                .monthlyPayment(BigDecimal.valueOf(100.0))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(userId)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        when(loanService.updateLoan(any(LoanDto.class), any(Authentication.class))).thenThrow(new NoSuchElementException("Клиент с id " + userId + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto))
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Клиент с id " + userId + " не найден."));
        verify(loanService, times(1)).updateLoan(any(LoanDto.class), any(Authentication.class));
    }

    @Test
    public void testUpdateLoanWhenAccountBelongsToAnotherUser() throws Exception {
        Long accountId = 1L;
        Long userId = 10L;
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(BigDecimal.valueOf(500.0))
                .interestRateLoan(BigDecimal.valueOf(3.5))
                .startDateLoan(LocalDate.parse("2025-03-01"))
                .endDateLoan(LocalDate.parse("2026-03-01"))
                .monthlyPayment(BigDecimal.valueOf(100.0))
                .statusLoan("ACTIVE")
                .accountId(accountId)
                .userId(userId)
                .build();
        Account account = new Account();
        User accountUser = new User();
        accountUser.setId(20L);
        account.setUser(accountUser);
        User user = new User();
        user.setId(userId);

        when(loanService.updateLoan(any(LoanDto.class), any(Authentication.class))).thenThrow(new IllegalStateException("Этот счет принадлежит другому клиенту."));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Этот счет принадлежит другому клиенту."))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println(responseBody);
                })
                .andReturn();
        verify(loanService, times(1)).updateLoan(any(LoanDto.class), any(Authentication.class));
    }

    @Test
    public void testUpdateLoanEmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println(responseBody);
                });
        verify(loanService, times(0)).updateLoan(any(LoanDto.class), any(Authentication.class));
    }

    @Test
    public void testUpdateLoanDatabaseError() throws Exception {
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(BigDecimal.valueOf(500.0))
                .interestRateLoan(BigDecimal.valueOf(3.5))
                .startDateLoan(LocalDate.parse("2025-03-01"))
                .endDateLoan(LocalDate.parse("2026-03-01"))
                .monthlyPayment(BigDecimal.valueOf(100.0))
                .statusLoan("ACTIVE")
                .accountId(1L)
                .userId(10L)
                .build();

        when(loanService.updateLoan(any(LoanDto.class), any(Authentication.class))).thenThrow(new DataAccessException("Connection failed") {
        });
        mockMvc.perform(MockMvcRequestBuilders.put("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto))
                        .principal(mock(Authentication.class)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error: Connection failed"));
        verify(loanService, times(1)).updateLoan(any(LoanDto.class), any(Authentication.class));
    }

    @Test
    public void testDeleteLoanSuccess() throws Exception {
        Long id = 6L;

        when(loanRepository.existsById(id)).thenReturn(true);
        when(loanService.deleteLoan(id)).thenReturn("Кредит с id " + id + " был удален.");
        mockMvc.perform(MockMvcRequestBuilders.delete("/loans/{id}", id))
                .andExpect(status().isOk());
        verify(loanService, times(1)).deleteLoan(id);
    }

    @Test
    public void testDeleteLoanWhenIdNotFound() throws Exception {
        Long id = 777L;

        when(loanRepository.existsById(id)).thenReturn(false);
        when(loanService.deleteLoan(id)).thenThrow(new NoSuchElementException("Кредит с id " + id + " не найден."));
        mockMvc.perform(MockMvcRequestBuilders.delete("/loans/{id}", id))
                .andExpect(status().isNotFound());
        verify(loanService, times(0)).deleteLoan(id);
    }

    @Test
    public void testDeleteLoanDatabaseError() throws Exception {
        Long id = 6L;

        when(loanRepository.existsById(id)).thenReturn(true);
        when(loanService.deleteLoan(id)).thenThrow(new DataAccessException("Connection failed") {
        });
        mockMvc.perform(MockMvcRequestBuilders.delete("/loans/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database error: Connection failed"));
        verify(loanService, times(1)).deleteLoan(id);
    }
}