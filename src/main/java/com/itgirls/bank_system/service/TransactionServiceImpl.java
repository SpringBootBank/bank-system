package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.TransactionDto;
import com.itgirls.bank_system.enums.TransactionType;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Transactions;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.TransactionRepository;
import com.itgirls.bank_system.repository.UserRepository;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.mapper.EntityToDtoMapper;
import java.time.LocalDateTime;

import com.itgirls.bank_system.specification.TransactionSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionDto addNewTransaction(TransactionDto transactionDto) {
        if (transactionDto.getSenderAccountId() == null || transactionDto.getBeneficiaryAccountId() == null) {
            throw new RuntimeException("Отправитель и получатель должны быть указаны");
        }

        Account sender = accountRepository.findById(transactionDto.getSenderAccountId())
                .orElseThrow(() -> new RuntimeException("Отправитель не найден"));
        Account beneficiary = accountRepository.findById(transactionDto.getBeneficiaryAccountId())
                .orElseThrow(() -> new RuntimeException("Получатель не найден"));
        User user = userRepository.findById(transactionDto.getBankUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Transactions transaction = Transactions.builder()
                .transactionNumber("TXN-" + UUID.randomUUID())
                .transactionType(TransactionType.valueOf(transactionDto.getTransactionType()))
                .transactionAmount(transactionDto.getTransactionAmount())
                .transactionTime(LocalDateTime.now())
                .senderAccount(sender)
                .beneficiaryAccount(beneficiary)
                .bankUser(user)
                .build();

        if (transaction.getTransactionType().name().equals("OUTGOING") && sender.getBalance()
                .compareTo(transaction.getTransactionAmount()) < 0) {
            throw new RuntimeException("Недостаточно средств для проведения транзакции");
        }
        Transactions savedTransaction = transactionRepository.save(transaction);
        log.info("Добавили новую транзакцию");

        sender.setBalance(sender.getBalance().subtract(savedTransaction.getTransactionAmount()));
        beneficiary.setBalance(beneficiary.getBalance().add(savedTransaction.getTransactionAmount()));

        accountRepository.save(sender);
        log.info("Обновили баланс счета отправителя");

        accountRepository.save(beneficiary);
        log.info("Обновили баланс счета получателя");

        return EntityToDtoMapper.convertTransactionToDto(savedTransaction);
    }

    @Override
    public List<TransactionDto> findAllTransactions() {
        List<Transactions> transactions = transactionRepository.findAll();
        log.info("Нашли {} транзакций", transactions.size());

        return EntityToDtoMapper.convertTransactionToDto(transactions);
    }

    @Override
    public List<TransactionDto> findByBankUser_Id(Long userId) {
        List<Transactions> transactions = transactionRepository.findByBankUser_Id(userId);

        if (transactions.isEmpty()) {
            throw new RuntimeException("Транзакции для пользователя с id " + userId + " не найдены");
        }
        log.info("Нашли {} транзакций для пользователя с id: {}", transactions.size(), userId);

        return EntityToDtoMapper.convertTransactionToDto(transactions);
    }

    @Transactional
    public TransactionDto updateAmountOfTransaction(TransactionDto transactionDto) {
        Transactions transaction = transactionRepository.findById(transactionDto.getId())
                .orElseThrow(() -> new RuntimeException("Транзакция с id " + transactionDto.getId() + " не найдена"));
        Account account = accountRepository.findById(transactionDto.getBankUserId())
                .orElseThrow(() -> new RuntimeException("Счет не найден"));

        BigDecimal oldAmountTransac = transaction.getTransactionAmount();

        BigDecimal newAmountTransac = transactionDto.getTransactionAmount();

        BigDecimal difference = newAmountTransac.subtract(oldAmountTransac);

        boolean isOutgoing = transaction.getTransactionType().name().equals("OUTGOING");

        if (isOutgoing && difference.compareTo(BigDecimal.ZERO) > 0
                && (difference.compareTo(account.getBalance()) > 0)) {
            throw new RuntimeException("Средств на счете не достаточно");
        }

        if (isOutgoing) {
            account.setBalance(account.getBalance().subtract(difference));
        } else {
            account.setBalance(account.getBalance().add(difference));
            log.info("Обновили баланс счета с id: {}", account.getId());
        }

        transaction.setTransactionAmount(newAmountTransac);

        transactionRepository.save(transaction);
        log.info("Обновили сумму транзакции с id: {}", transaction.getId());

        accountRepository.save(account);
        log.info("Обновили баланс счета с id: {}", account.getId());

        return EntityToDtoMapper.convertTransactionToDto(transaction);
    }

    public String deleteTransaction(Long id) throws NoSuchElementException {
        try {
            transactionRepository.deleteById(id);
            log.info("Транзакция с id {} успешно удалена", id);
            return "Транзакция с id " + id + " успешно удалена";
        } catch (Exception e) {
            log.error("Ошибка при удалении транзакции с id {}: {}", id, e.getMessage());
            throw new NoSuchElementException("Ошибка при удалении транзакции с id :" + id);
        }
    }

    public List<Transactions> getTransactionByTypeOrSenderOrBeneficiary(String type, Long senderAccountId,
                                                                        Long beneficiaryAccountId) {
        Specification<Transactions> specification = Specification.where(null);
        log.info("Фильтрация: type={}, senderAccountId={}, beneficiaryAccountId={}", type, senderAccountId, beneficiaryAccountId);

        if (StringUtils.isNotEmpty(type)) {
            try {
                type = type.toUpperCase();
                TransactionType.valueOf(type);
            } catch (IllegalArgumentException e) {
                log.error("Ошибка: Некорректный тип транзакции: {}", type);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Некорректный тип транзакции. Разрешены INCOMING или OUTGOING");
            }
        }

        specification = specification.and(TransactionSpecification.hasType(type));

        if (senderAccountId != null) {
            if (!transactionRepository.existsById(senderAccountId)) {
                log.error("Ошибка: отправитель с указанным id не найден");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Отправитель с указанным id в бд не найден ");
            }

            specification = specification.and(TransactionSpecification.hasSenderId(senderAccountId));
        }
        if (beneficiaryAccountId != null) {
            if (!transactionRepository.existsById(beneficiaryAccountId)) {
                log.error("Ошибка: получатель с указанным id не найден");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Получатель с указанным id в бд не найден ");
            }

            specification = specification.and(TransactionSpecification.hasBeneficiaryId(beneficiaryAccountId));
        }
        List<Transactions> transactions = transactionRepository.findAll(specification);
        log.info("После фильтрации найдено {} транзакций", transactions.size());
        return transactions;
    }
}

