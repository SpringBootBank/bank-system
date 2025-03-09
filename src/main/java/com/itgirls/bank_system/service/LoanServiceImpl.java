package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.LoanDto;
import com.itgirls.bank_system.enums.LoanStatus;
import com.itgirls.bank_system.enums.Role;
import com.itgirls.bank_system.mapper.EntityToDtoMapper;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Loan;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.LoanRepository;
import com.itgirls.bank_system.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.itgirls.bank_system.mapper.EntityToDtoMapper.convertLoanEntityToDto;

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
            LoanDto loanDto = convertLoanEntityToDto(loan.get());
            log.info("Кредит: {}.", loanDto.toString());
            return loanDto;
        } else {
            log.error("Кредит с id {} не найден.", id);
            throw new NoSuchElementException("Кредит с id " + id + " не найден.");
        }
    }

    @Override
    public List<LoanDto> getLoansByStatusAndUserID(String statusLoan, Long userId) {
        log.info("Поиск кредитов по статусу {} и клиенту {}.", statusLoan, userId);
        if (statusLoan == null || !statusLoan.matches("^(ACTIVE|CLOSED|OVERDUE)$")) {
            log.error("Неверный статус кредита: {}. Статус должен быть ACTIVE, CLOSED или OVERDUE.", statusLoan);
            throw new IllegalArgumentException("Статус кредита должен быть ACTIVE, CLOSED или OVERDUE.");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден.");
        }
        Specification<Loan> specification = (root, query, criteriaBuilder) -> {
            Predicate statusPredicate = criteriaBuilder.equal(root.get("statusLoan"), statusLoan);
            Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
            return criteriaBuilder.and(statusPredicate, userPredicate);
        };
        List<Loan> loans = loanRepository.findAll(specification);
        if (loans.isEmpty()) {
            log.warn("Кредиты со статусом {} и клиентом {} не найдены.", statusLoan, userId);
            return Collections.emptyList();
        }
        log.info("Кредиты со статусом {} и клиентом {} найдены.", statusLoan, userId);
        return loans.stream()
                .map(EntityToDtoMapper::convertLoanEntityToDto)
                .toList();
    }

    @Override
    public List<LoanDto> getAllLoans() {
        log.info("Поиск всех кредитов.");
        List<Loan> loans = loanRepository.findAll();
        log.info("Найдено {} кредитов.", loans.size());
        return loans.stream()
                .map(EntityToDtoMapper::convertLoanEntityToDto)
                .toList();
    }

    @Override
    public LoanDto createLoan(LoanDto loanDto) {
        log.info("Создание нового кредита.");
        Loan loan = loanRepository.save(convertLoanDtoToEntity(loanDto));
        loanDto = convertLoanEntityToDto(loan);
        log.info("Кредит с id {} был создан.", loan.getId());
        return loanDto;
    }

    private Loan convertLoanDtoToEntity(LoanDto loanDto) {
        Account account = accountRepository.findById(loanDto.getAccountId())
                .orElseThrow(() -> new NoSuchElementException("Счет с id " + loanDto.getAccountId() + " не найден."));
        User user = userRepository.findById(loanDto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Клиент с id " + loanDto.getUserId() + " не найден."));
        if (!user.getId().equals(account.getUser().getId())) {
            throw new IllegalStateException("Этот счет принадлежит другому клиенту.");
        } else {
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
    }

    @Override
    public LoanDto updateLoan(LoanDto loanDto, Authentication authentication) {
        log.info("Обновление данных по кредиту с id {}.", loanDto.getId());
        Loan loan = loanRepository.findById(loanDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Кредит с id " + loanDto.getId() + " не найден."));
        Account account = accountRepository.findById(loanDto.getAccountId())
                .orElseThrow(() -> new NoSuchElementException("Счет с id " + loanDto.getAccountId() + " не найден."));
        User user = Optional.ofNullable(userRepository.findUserByEmail(authentication.getName()))
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        if (user.getRole() == Role.ADMIN) {
            user = userRepository.findById(loanDto.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("Клиент с id " + loanDto.getUserId() + " не найден."));
        }
        if (!user.getId().equals(account.getUser().getId())) {
            throw new IllegalStateException("Этот счет принадлежит другому клиенту.");
        } else {
            loan.setAmountLoan(loanDto.getAmountLoan());
            loan.setInterestRateLoan(loanDto.getInterestRateLoan());
            loan.setStartDateLoan(loanDto.getStartDateLoan());
            loan.setEndDateLoan(loanDto.getEndDateLoan());
            loan.setMonthlyPayment(loanDto.getMonthlyPayment());
            loan.setStatusLoan(LoanStatus.valueOf(loanDto.getStatusLoan()));
            loan.setAccount(account);
            loan.setUser(user);
        }
        Loan savedLoan = loanRepository.save(loan);
        LoanDto loanDtoSave = convertLoanEntityToDto(savedLoan);
        log.info("Кредит: {} был обновлен.", loanDtoSave);
        return loanDtoSave;
    }

    @Override
    public String deleteLoan(Long id) {
        log.info("Удаление кредита с id {}.", id);
        if (!loanRepository.existsById(id)) {
            log.warn("Кредит с id {} не найден, удаление невозможно.", id);
            throw new NoSuchElementException("Кредит с id " + id + " не найден.");
        }
        loanRepository.deleteById(id);
        log.info("Кредит с id {} был удален.", id);
        return "Кредит с id " + id + " был удален.";
    }
}