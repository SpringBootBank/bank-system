package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.enums.LoanStatus;
import com.itgirls.bank_system.enums.Role;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Loan;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.LoanRepository;
import com.itgirls.bank_system.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
public class LoanServiceTest {
    @Mock
    LoanRepository loanRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    LoanServiceImpl loanService;

    @Test
    public void testGetLoanByIdSuccessfully() {
        Long id = 1L;
        BigDecimal amountLoan = BigDecimal.valueOf(300);
        BigDecimal interestRateLoan = BigDecimal.valueOf(2);
        LocalDate startDateLoan = LocalDate.parse("2025-02-10");
        LocalDate endDateLoan = LocalDate.parse("2025-08-10");
        BigDecimal monthlyPayment = BigDecimal.valueOf(50.29);
        LoanStatus statusLoan = LoanStatus.ACTIVE;
        Account account = new Account();
        User user = new User();
        Loan loan = new Loan(id, amountLoan, interestRateLoan, startDateLoan, endDateLoan, monthlyPayment, statusLoan, account, user);

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        LoanDto loanDto = loanService.getLoanById(id);
        Assertions.assertEquals(loanDto.getId(), loan.getId());
        Assertions.assertEquals(loanDto.getAmountLoan(), loan.getAmountLoan());
        Assertions.assertEquals(loanDto.getInterestRateLoan(), loan.getInterestRateLoan());
        Assertions.assertEquals(loanDto.getStartDateLoan(), loan.getStartDateLoan());
        Assertions.assertEquals(loanDto.getEndDateLoan(), loan.getEndDateLoan());
        Assertions.assertEquals(loanDto.getMonthlyPayment(), loan.getMonthlyPayment());
        Assertions.assertEquals(loanDto.getStatusLoan(), loan.getStatusLoan().name());
        Assertions.assertEquals(loanDto.getAccountId(), loan.getAccount().getId());
        Assertions.assertEquals(loanDto.getUserId(), loan.getUser().getId());
        verify(loanRepository, times(1)).findById(id);
    }

    @Test
    public void testGetLoanByIdWhenIdNotFound() {
        Long id = 777L;

        when(loanRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> loanService.getLoanById(id));
        verify(loanRepository, times(1)).findById(id);
    }

    @Test
    public void testGetLoansByStatusAndUserIdSuccessfully() {
        User user = new User();
        user.setId(10L);
        Account account = new Account();
        account.setId(10L);
        account.setUser(user);
        Loan loan1 = Loan.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan(LoanStatus.ACTIVE)
                .account(account)
                .user(user)
                .build();
        Loan loan2 = Loan.builder()
                .id(2L)
                .amountLoan(new BigDecimal(500))
                .interestRateLoan(new BigDecimal(5))
                .startDateLoan(LocalDate.parse("2025-01-20"))
                .endDateLoan(LocalDate.parse("2026-01-20"))
                .monthlyPayment(BigDecimal.valueOf(42.8))
                .statusLoan(LoanStatus.ACTIVE)
                .account(account)
                .user(user)
                .build();
        List<Loan> loans = List.of(loan1, loan2);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(loanRepository.findAll(any(Specification.class))).thenReturn(loans);

        List<LoanDto> loanDtos = loanService.getLoansByStatusAndUserID("ACTIVE", user.getId());
        Assertions.assertNotNull(loanDtos);
        Assertions.assertEquals(2, loanDtos.size());
        Assertions.assertEquals(1L, loanDtos.get(0).getId());
        Assertions.assertEquals(new BigDecimal(300), loanDtos.get(0).getAmountLoan());
        Assertions.assertEquals(new BigDecimal(2), loanDtos.get(0).getInterestRateLoan());
        Assertions.assertEquals(LocalDate.parse("2025-02-10"), loanDtos.get(0).getStartDateLoan());
        Assertions.assertEquals(LocalDate.parse("2025-08-10"), loanDtos.get(0).getEndDateLoan());
        Assertions.assertEquals(BigDecimal.valueOf(50.29), loanDtos.get(0).getMonthlyPayment());
        Assertions.assertEquals("ACTIVE", loanDtos.get(0).getStatusLoan());
        Assertions.assertEquals(2L, loanDtos.get(1).getId());
        Assertions.assertEquals(new BigDecimal(500), loanDtos.get(1).getAmountLoan());
        Assertions.assertEquals(new BigDecimal(5), loanDtos.get(1).getInterestRateLoan());
        Assertions.assertEquals(LocalDate.parse("2025-01-20"), loanDtos.get(1).getStartDateLoan());
        Assertions.assertEquals(LocalDate.parse("2026-01-20"), loanDtos.get(1).getEndDateLoan());
        Assertions.assertEquals(BigDecimal.valueOf(42.8), loanDtos.get(1).getMonthlyPayment());
        Assertions.assertEquals("ACTIVE", loanDtos.get(1).getStatusLoan());
        verify(userRepository, times(1)).findById(user.getId());
        verify(loanRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    public void testGetLoansByStatusAndUserIdEmptyList() {
        User user = new User();
        user.setId(10L);
        String statusLoan = "ACTIVE";

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(loanRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<LoanDto> loanDtos = loanService.getLoansByStatusAndUserID(statusLoan, user.getId());
        Assertions.assertNotNull(loanDtos);
        Assertions.assertTrue(loanDtos.isEmpty());
        verify(userRepository, times(1)).findById(user.getId());
        verify(loanRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    public void testGetLoansByStatusAndUserIdWhenWrongStatus() {
        Long userId = 10L;
        String loanStatus = "WRONG";

        Assertions.assertThrows(IllegalArgumentException.class, () -> loanService.getLoansByStatusAndUserID(loanStatus, userId));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(loanRepository);
    }

    @Test
    public void testGetLoansByStatusAndUserIdWhenUserNotFound() {
        Long userId = 777L;
        String statusLoan = "ACTIVE";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> loanService.getLoansByStatusAndUserID(statusLoan, userId));
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(loanRepository);
    }

    @Test
    public void testGetAllLoansSuccessfully() {
        Loan loan1 = Loan.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan(LoanStatus.ACTIVE)
                .account(new Account())
                .user(new User())
                .build();
        Loan loan2 = Loan.builder()
                .id(2L)
                .amountLoan(new BigDecimal(500))
                .interestRateLoan(new BigDecimal(5))
                .startDateLoan(LocalDate.parse("2025-01-20"))
                .endDateLoan(LocalDate.parse("2026-01-20"))
                .monthlyPayment(BigDecimal.valueOf(42.8))
                .statusLoan(LoanStatus.ACTIVE)
                .account(new Account())
                .user(new User())
                .build();
        List<Loan> loans = List.of(loan1, loan2);

        when(loanRepository.findAll()).thenReturn(loans);

        List<LoanDto> loanDtos = loanService.getAllLoans();
        Assertions.assertEquals(2, loanDtos.size());
        Assertions.assertEquals(1L, loanDtos.get(0).getId());
        Assertions.assertEquals(new BigDecimal(300), loanDtos.get(0).getAmountLoan());
        Assertions.assertEquals(new BigDecimal(2), loanDtos.get(0).getInterestRateLoan());
        Assertions.assertEquals(LocalDate.parse("2025-02-10"), loanDtos.get(0).getStartDateLoan());
        Assertions.assertEquals(LocalDate.parse("2025-08-10"), loanDtos.get(0).getEndDateLoan());
        Assertions.assertEquals(BigDecimal.valueOf(50.29), loanDtos.get(0).getMonthlyPayment());
        Assertions.assertEquals("ACTIVE", loanDtos.get(0).getStatusLoan());
        Assertions.assertEquals(2L, loanDtos.get(1).getId());
        Assertions.assertEquals(new BigDecimal(500), loanDtos.get(1).getAmountLoan());
        Assertions.assertEquals(new BigDecimal(5), loanDtos.get(1).getInterestRateLoan());
        Assertions.assertEquals(LocalDate.parse("2025-01-20"), loanDtos.get(1).getStartDateLoan());
        Assertions.assertEquals(LocalDate.parse("2026-01-20"), loanDtos.get(1).getEndDateLoan());
        Assertions.assertEquals(BigDecimal.valueOf(42.8), loanDtos.get(1).getMonthlyPayment());
        Assertions.assertEquals("ACTIVE", loanDtos.get(1).getStatusLoan());
        verify(loanRepository, times(1)).findAll();
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    public void testGetAllLoansEmptyList() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<LoanDto> loanDtos = loanService.getAllLoans();
        Assertions.assertEquals(0, loanDtos.size());
        verify(loanRepository, times(1)).findAll();
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    public void testCreateLoanSuccessfully() {
        User user = new User();
        user.setId(10L);
        Account account = new Account();
        account.setId(10L);
        account.setUser(user);
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(account.getId())
                .userId(user.getId())
                .build();
        Loan loan = Loan.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan(LoanStatus.ACTIVE)
                .account(account)
                .user(user)
                .build();
        LoanDto loanDtoExpected = LoanDto.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(10L)
                .userId(10L)
                .build();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanDto loanDtoResult = loanService.createLoan(loanDto);
        Assertions.assertEquals(loanDtoExpected, loanDtoResult);
        verify(accountRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(loanRepository, times(1)).save(any(Loan.class));
        verifyNoMoreInteractions(loanRepository, accountRepository, userRepository);
    }

    @Test
    public void testConvertLoanDtoToEntityWhenAccountNotFound() {
        Long accountId = 1L;
        Long userId = 10L;
        LoanDto loanDto = new LoanDto();
        loanDto.setAccountId(accountId);
        loanDto.setUserId(userId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> loanService.convertLoanDtoToEntity(loanDto));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Счет с id " + accountId + " не найден.", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(userRepository);
    }

    @Test
    public void testConvertLoanDtoToEntityWhenUserNotFound() {
        Long accountId = 1L;
        Long userId = 10L;
        LoanDto loanDto = new LoanDto();
        loanDto.setAccountId(accountId);
        loanDto.setUserId(userId);
        Account account = new Account();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> loanService.convertLoanDtoToEntity(loanDto));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Клиент с id " + userId + " не найден.", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(loanRepository);
    }

    @Test
    public void testConvertLoanDtoToEntityWhenAccountBelongsToAnotherUser() {
        Long accountId = 1L;
        Long userId = 10L;
        LoanDto loanDto = new LoanDto();
        loanDto.setAccountId(accountId);
        loanDto.setUserId(userId);
        Account account = new Account();
        User accountUser = new User();
        accountUser.setId(20L);
        account.setUser(accountUser);
        User user = new User();
        user.setId(userId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> loanService.convertLoanDtoToEntity(loanDto));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Этот счет принадлежит другому клиенту.", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUpdateLoanSuccessfullyByClient() {
        User user = new User();
        user.setId(10L);
        user.setEmail("client@test.com");
        user.setRole(Role.CLIENT);
        Account account = new Account();
        account.setId(10L);
        account.setUser(user);
        Loan loan = Loan.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan(LoanStatus.ACTIVE)
                .account(account)
                .user(user)
                .build();
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-03-10"))
                .endDateLoan(LocalDate.parse("2025-09-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(10L)
                .userId(10L)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("client@test.com");
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(userRepository.findUserByEmail("client@test.com")).thenReturn(user);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanDto updatedLoan = loanService.updateLoan(loanDto, authentication);
        Assertions.assertNotNull(updatedLoan);
        Assertions.assertEquals(loanDto.getAmountLoan(), updatedLoan.getAmountLoan());
        Assertions.assertEquals(loanDto.getInterestRateLoan(), updatedLoan.getInterestRateLoan());
        Assertions.assertEquals(loanDto.getStartDateLoan(), updatedLoan.getStartDateLoan());
        Assertions.assertEquals(loanDto.getEndDateLoan(), updatedLoan.getEndDateLoan());
        Assertions.assertEquals(loanDto.getMonthlyPayment(), updatedLoan.getMonthlyPayment());
        Assertions.assertEquals(loanDto.getStatusLoan(), updatedLoan.getStatusLoan());
        verify(loanRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findUserByEmail("client@test.com");
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoanSuccessfullyByAdmin() {
        User user = new User();
        user.setId(10L);
        user.setEmail("admin@test.com");
        user.setRole(Role.ADMIN);
        Account account = new Account();
        account.setId(10L);
        account.setUser(user);
        Loan loan = Loan.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-02-10"))
                .endDateLoan(LocalDate.parse("2025-08-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan(LoanStatus.ACTIVE)
                .account(account)
                .user(user)
                .build();
        LoanDto loanDto = LoanDto.builder()
                .id(1L)
                .amountLoan(new BigDecimal(300))
                .interestRateLoan(new BigDecimal(2))
                .startDateLoan(LocalDate.parse("2025-03-10"))
                .endDateLoan(LocalDate.parse("2025-09-10"))
                .monthlyPayment(BigDecimal.valueOf(50.29))
                .statusLoan("ACTIVE")
                .accountId(10L)
                .userId(10L)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(userRepository.findUserByEmail("admin@test.com")).thenReturn(user);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanDto updatedLoan = loanService.updateLoan(loanDto, authentication);
        Assertions.assertNotNull(updatedLoan);
        Assertions.assertEquals(loanDto.getAmountLoan(), updatedLoan.getAmountLoan());
        Assertions.assertEquals(loanDto.getInterestRateLoan(), updatedLoan.getInterestRateLoan());
        Assertions.assertEquals(loanDto.getStartDateLoan(), updatedLoan.getStartDateLoan());
        Assertions.assertEquals(loanDto.getEndDateLoan(), updatedLoan.getEndDateLoan());
        Assertions.assertEquals(loanDto.getMonthlyPayment(), updatedLoan.getMonthlyPayment());
        Assertions.assertEquals(loanDto.getStatusLoan(), updatedLoan.getStatusLoan());
        Assertions.assertEquals(loanDto.getAccountId(), updatedLoan.getAccountId());
        Assertions.assertEquals(loanDto.getUserId(), updatedLoan.getUserId());
        verify(loanRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findUserByEmail("admin@test.com");
        verify(userRepository, times(1)).findById(10L);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoanWhenLoanNotFound() {
        Long id = 1L;
        LoanDto loanDto = new LoanDto();
        loanDto.setId(id);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(loanRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> loanService.updateLoan(loanDto, authentication));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Кредит с id " + id + " не найден.", exception.getMessage());
        verify(loanRepository, times(1)).findById(id);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoanWhenAccountNotFound() {
        Long id = 1L;
        Long accountId = 10L;
        LoanDto loanDto = new LoanDto();
        loanDto.setId(id);
        loanDto.setAccountId(accountId);
        Loan loan = new Loan();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> loanService.updateLoan(loanDto, authentication));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Счет с id " + accountId + " не найден.", exception.getMessage());
        verify(loanRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).findById(accountId);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoanWhenUserNotFound() {
        Long id = 1L;
        Long accountId = 10L;
        LoanDto loanDto = new LoanDto();
        loanDto.setId(id);
        loanDto.setAccountId(accountId);
        Loan loan = new Loan();
        Account account = new Account();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(userRepository.findUserByEmail("user@test.com")).thenReturn(null);

        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> loanService.updateLoan(loanDto, authentication));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
        verify(loanRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).findById(accountId);
        verify(userRepository, times(1)).findUserByEmail("user@test.com");
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void testUpdateLoanWhenAccountBelongsToAnotherUser() {
        Long id = 1L;
        Long accountId = 10L;
        Long userId = 10L;
        Long accountOwnerId = 20L;
        LoanDto loanDto = new LoanDto();
        loanDto.setId(id);
        loanDto.setAccountId(accountId);
        loanDto.setUserId(userId);
        Loan loan = new Loan();
        Account account = new Account();
        User accountOwner = new User();
        accountOwner.setId(accountOwnerId);
        account.setUser(accountOwner);
        User user = new User();
        user.setId(userId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(userRepository.findUserByEmail("admin@test.com")).thenReturn(user);

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> loanService.updateLoan(loanDto, authentication));
        System.out.println("Ошибка: " + exception.getMessage());
        Assertions.assertEquals("Этот счет принадлежит другому клиенту.", exception.getMessage());
        verify(loanRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).findById(accountId);
        verify(userRepository, times(1)).findUserByEmail("admin@test.com");
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void testDeleteLoanSuccessfully() {
        Long id = 1L;

        when(loanRepository.existsById(id)).thenReturn(true);

        loanService.deleteLoan(id);
        verify(loanRepository, times(1)).existsById(id);
        verify(loanRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteLoanWhenIdNotFound() {
        Long id = 1L;

        when(loanRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(NoSuchElementException.class, () -> loanService.deleteLoan(id));
        verify(loanRepository, times(1)).existsById(id);
        verify(loanRepository, never()).deleteById(anyLong());
    }
}