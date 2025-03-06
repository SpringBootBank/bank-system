package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.enums.LoanStatus;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Loan;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.LoanRepository;
import com.itgirls.bank_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public LoanDto getLoanById(Long id) {
        log.info("Поиск кредита по id {}.", id);
        Optional<Loan> loan = loanRepository.findById(id);
        if (loan.isPresent()) {
            LoanDto loanDto = convertEntityToDto(loan.get());
            log.info("Кредит: {}.", loanDto.toString());
            return loanDto;
        } else {
            log.error("Кредит с id {} не найден.", id);
            throw new NoSuchElementException("Кредит с id " + id + " не найден.");
        }
    }

    private LoanDto convertEntityToDto(Loan loan) {
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

    @Override
    public List<LoanDto> getLoansByStatus(String statusLoan) {
        log.info("Поиск кредитов по статусу {}.", statusLoan);
        try {
            Specification<Loan> specification = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("statusLoan"), statusLoan);
            List<Loan> loans = loanRepository.findAll(specification);
            if (loans.isEmpty()) {
                log.warn("Кредиты со статусом {} не найдены.", statusLoan);
                return Collections.emptyList();
            }
            log.info("Кредиты со статусом {} найдены.", statusLoan);
            List<LoanDto> loansDto = loans.stream()
                    .map(this::convertEntityToDto)
                    .toList();
            log.info("Кредиты: {}.", loansDto);
            return loansDto;
        } catch (Exception e) {
            log.error("Ошибка при поиске кредитов со статусом {}: {}.", statusLoan, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<LoanDto> getAllLoans() {
        log.info("Поиск всех кредитов.");
        try {
            List<Loan> loans = loanRepository.findAll();
            log.info("{} кредиты найдены.", loans.size());
            return loans.stream().map(this::convertEntityToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при поиске кредитов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public LoanDto createLoan(LoanDto loanDto) {
        log.info("Создание нового кредита.");
        try {
            Loan loan = loanRepository.save(convertDtoToEntity(loanDto));
            loanDto = convertEntityToDto(loan);
            log.info("Кредит с id {} был создан.", loan.getId());
            return loanDto;
        } catch (Exception e) {
            log.error("Ошибка при создании нового кредита: {}", e.getMessage());
            throw e;
        }
    }

    private Loan convertDtoToEntity(LoanDto loanDto) {
        Account account = accountRepository.findById(loanDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Счет не найден."));
        User user = userRepository.findById(loanDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Клиент не найден."));
        return Loan.builder()
                .amountLoan(loanDto.getAmountLoan())
                .interestRateLoan(loanDto.getInterestRateLoan())
                .startDateLoan(loanDto.getStartDateLoan())
                .endDateLoan(loanDto.getEndDateLoan())
                .monthlyPayment(loanDto.getMonthlyPayment())
                .statusLoan(LoanStatus.valueOf(loanDto.getStatusLoan()))
                .account(account)
                .user(user)
                .build();
    }

    @Override
    public LoanDto updateLoan(LoanDto loanDto) {
        log.info("Обновление данных по кредиту с id {}.", loanDto.getId());
        Account account = accountRepository.findById(loanDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
       User user = userRepository.findById(loanDto.getUserId())
               .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Loan> loanOptional = loanRepository.findById(loanDto.getId());
        if (loanOptional.isPresent()) {
            Loan loan = loanOptional.get();
            loan.setAmountLoan(loanDto.getAmountLoan());
            loan.setInterestRateLoan(loanDto.getInterestRateLoan());
            loan.setStartDateLoan(loanDto.getStartDateLoan());
            loan.setEndDateLoan(loanDto.getEndDateLoan());
            loan.setMonthlyPayment(loanDto.getMonthlyPayment());
            loan.setStatusLoan(LoanStatus.valueOf(loanDto.getStatusLoan()));
            loan.setAccount(account);
           loan.setUser(user);
            Loan savedLoan = loanRepository.save(loan);
            LoanDto loanDtoSave = convertEntityToDto(savedLoan);
            log.info("Кредит: {} был обновлен.", loanDtoSave.toString());
            return loanDtoSave;
        } else {
            log.error("Кредит с id {} не найден.", loanDto.getId());
            throw new NoSuchElementException("Кредит с id " + loanDto.getId() + " не найден.");
        }
    }

    @Override
    public String deleteLoan(Long id) {
        log.info("Удаление крдеита с id {}.", id);
        if (!loanRepository.existsById(id)) {
            log.warn("Кредит с id {} не найде, удаление невозможно.", id);
            throw new NoSuchElementException("Кредит с id " + id + " не найден.");
        }
        loanRepository.deleteById(id);
        log.info("Кредит с id {} был удален.", id);
        return "Кредит с id " + id + " был удален.";
    }
}