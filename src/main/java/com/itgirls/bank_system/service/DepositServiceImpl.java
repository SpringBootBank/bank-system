package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.enums.DepositStatus;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.DepositRepository;
import com.itgirls.bank_system.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    //private final AccountRepository accountRepository;

    @Override
    public DepositDto createDeposit(DepositDto depositDto) {
        log.info("Открытие нового вклада.");
        Deposit newDeposit = new Deposit();
        //newDeposit.setAccount(accountRepository.getReferenceById(depositDto.getAccount().getId()));
        newDeposit.setAmountDeposit(depositDto.getAmountDeposit());
        newDeposit.setStartDateDeposit(depositDto.getStartDateDeposit());
        newDeposit.setEndDateDeposit(depositDto.getEndDateDeposit());
        newDeposit.setInterestRateDeposit(depositDto.getInterestRateDeposit());
        newDeposit.setStatusDeposit(DepositStatus.valueOf(depositDto.getStatusDeposit()));

        depositRepository.save(newDeposit);
        return convertDepositToDto(newDeposit);
    }

    @Override
    public List<DepositDto> getAllDeposits() {
        log.info("Поиск списка всех вкладов.");
        List<Deposit> depositList = depositRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        log.info("Всего найдено вкладов: {}", depositList.size());

        List<CompletableFuture<DepositDto>> futures = new ArrayList<>();

        for (Deposit deposit : depositList) {
            CompletableFuture<DepositDto> future = CompletableFuture.supplyAsync(() -> {
                log.info("Обрабатываем депозит с ID: {}", deposit.getId());
                return convertDepositToDto(deposit);
            });
            futures.add(future);
        }
        List<DepositDto> depositDtoList = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        log.info("Всего преобразовано вкладов: {}", depositDtoList.size());
        return depositDtoList;
    }

    @Override
    public DepositDto getDepositById(Long id) {
        log.info("Поиск вклада по ID.");
        try {
            Deposit deposit = depositRepository.getReferenceById(id);
            log.info("Вклад с ID {} найден.", id);
            return convertDepositToDto(deposit);
        } catch (Exception e) {
            log.error("Вклад с ID {} не найден.", id);
            return null;
        }
    }

    public List<DepositDto> getDepositsByUserId(Long id) {
        log.info("Поиск вклада(ов) по ID пользователя.");
        try {
            User user = userRepository.getReferenceById(id);
            Set<Deposit> userDeposits = user.getDeposits();

            if(!userDeposits.isEmpty()) {
                log.info("Вклады пользователя с ID {} найдены.", id);
                List<DepositDto> userDepositsDtoList = new ArrayList<>();
                for(Deposit deposit : userDeposits) {
                    DepositDto depositDto = convertDepositToDto(deposit);
                    userDepositsDtoList.add(depositDto);
                }
                return userDepositsDtoList;
            } else {
                log.info("Вклады пользователя с ID {} не найдены.", id);
                System.out.println("Вклады пользователя с ID " + id + " не найдены.");
                return null;
            }

        } catch (Exception e) {
            log.error("Пользователь с ID {} не найден.", id);
            return null;
        }
    }

    @Override
    public Deposit updateDeposit(DepositDto depositDto) {
        log.info("Внесение изменений во вклад с ID: {}", depositDto.getId());
        Deposit deposit = depositRepository.getReferenceById(depositDto.getId());

        deposit.setStatusDeposit(StringUtils.isNotEmpty(depositDto.getStatusDeposit()) ?
                DepositStatus.valueOf(depositDto.getStatusDeposit()) : deposit.getStatusDeposit());
        deposit.setAmountDeposit(StringUtils.isNotEmpty(String.valueOf(depositDto.getAmountDeposit())) ?
                depositDto.getAmountDeposit() : deposit.getAmountDeposit());
        deposit.setInterestRateDeposit(StringUtils.isNotEmpty(String.valueOf(depositDto.getInterestRateDeposit())) ?
                depositDto.getInterestRateDeposit() : deposit.getInterestRateDeposit());
        deposit.setStartDateDeposit(StringUtils.isNotEmpty(String.valueOf(depositDto.getStartDateDeposit())) ?
                depositDto.getStartDateDeposit() : deposit.getStartDateDeposit());
        deposit.setEndDateDeposit(StringUtils.isNotEmpty(String.valueOf(depositDto.getEndDateDeposit())) ?
                depositDto.getEndDateDeposit() : deposit.getEndDateDeposit());

        log.info("Вклад с ID {} успешно изменен.", depositDto.getId());
        return depositRepository.save(deposit);
    }

    @Override
    public String deleteDeposit(Long id) {
        log.info("Удаление вклада с ID: {}", id);
        Deposit deposit = depositRepository.getReferenceById(id);
        try {
            depositRepository.delete(deposit);
            log.info("Вклад с ID {} успешно удален.", id);
            return "Вклад с ID " + id + " был удален.";
        } catch (Exception e) {
            log.error("Не удалось удалить вклад по ID: {}", id, e);
            return "Вклад с ID " + id + " не удалось удалить.";
        }
    }

    private DepositDto convertDepositToDto(Deposit deposit) {
        log.debug("Трансформация вклада с ID {} в DTO.", deposit.getId());
        return DepositDto.builder()
                .id(deposit.getId())
                .account(convertAccountToDto(deposit.getAccount()))
                .amountDeposit(deposit.getAmountDeposit())
                .startDateDeposit(deposit.getStartDateDeposit())
                .endDateDeposit(deposit.getEndDateDeposit())
                .interestRateDeposit(deposit.getInterestRateDeposit())
                .statusDeposit(String.valueOf(deposit.getStatusDeposit()))
                .build();
    }

    private AccountDto convertAccountToDto(Account account) {
        log.debug("Трансформация счета с ID {} в DTO.", account.getId());
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .build();
    }
}
