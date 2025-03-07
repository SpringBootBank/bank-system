package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.dto.DepositDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.enums.DepositStatus;
import com.itgirls.bank_system.enums.Role;
import com.itgirls.bank_system.exception.DepositNotFoundException;
import com.itgirls.bank_system.exception.FailedConvertToDtoException;
import com.itgirls.bank_system.exception.UserNotFoundException;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.AccountRepository;
import com.itgirls.bank_system.repository.DepositRepository;
import com.itgirls.bank_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @SneakyThrows
    @Override
    public DepositDto createDeposit(DepositDto depositDto, Authentication authentication) throws DataAccessException, FailedConvertToDtoException {
        log.info("Открытие нового вклада.");
        Account account = accountRepository.findById(depositDto.getAccount().getId())
                .orElseThrow(() -> {
                    log.warn("Счет с таким ID не найден.");
                    return new NoSuchElementException("Счет с таким ID не найден.");
                });
        User user = new User();
        try {
            user = userRepository.findUserByEmail(authentication.getName());
        } catch (NoSuchElementException e) {
                    log.warn("Пользователь с таким ID не найден.");
                    throw new UserNotFoundException("Пользователь с таким ID не найден.");
                }

        Deposit newDeposit = new Deposit();

        if(user.getRole() != Role.ADMIN) {
            newDeposit.setAmountDeposit(depositDto.getAmountDeposit());
            newDeposit.setInterestRateDeposit(depositDto.getInterestRateDeposit());
            newDeposit.setStartDateDeposit(depositDto.getStartDateDeposit());
            newDeposit.setEndDateDeposit(depositDto.getEndDateDeposit());
            newDeposit.setStatusDeposit(DepositStatus.valueOf(depositDto.getStatusDeposit()));
            newDeposit.setAccount(account);
            newDeposit.setUser(user);


            log.info("Сохранение нового вклада");
            depositRepository.save(newDeposit);
            log.info("Конвертация объекта в DTO");
            return convertDepositToDto(newDeposit);
        } else {
            System.out.println("Введите пользователя, на имя которого открывается вклад.");
            throw new IllegalArgumentException("При создании вклада админом нужно " +
                    "указать пользователя на имя которого открывается вклад.");
        }
    }

    @Override
    public List<DepositDto> getAllDeposits() throws FailedConvertToDtoException {
        log.info("Поиск списка всех вкладов.");
        List<Deposit> depositList = depositRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        log.info("Всего найдено вкладов: {}", depositList.size());

        List<CompletableFuture<DepositDto>> futureList = depositList.stream()
                .map(deposit -> CompletableFuture.supplyAsync(() -> {
                            log.info("Обрабатывается депозит с ID: {}", deposit.getId());
                            return convertDepositToDto(deposit);
                        })
                ).collect(Collectors.toList());

        List<DepositDto> depositDtoList = futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        log.info("Всего преобразовано вкладов: {}", depositDtoList.size());
        return depositDtoList;
    }

    @SneakyThrows
    @Override
    public DepositDto getDepositById(Long id) throws FailedConvertToDtoException {
        log.info("Поиск вклада по ID.");
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вклад с таким ID не найден.");
                    return new DepositNotFoundException("Вклад с таким ID не найден.");
                });
        log.info("Вклад с ID {} найден.", id);
        return convertDepositToDto(deposit);
    }

    @SneakyThrows
    public List<DepositDto> getDepositsByUserId(Long id) {
        log.info("Поиск вклада(ов) по ID пользователя.");
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с таким ID не найден.");
                    return new UserNotFoundException("Пользователь с таким ID не найден.");
                });
        Set<Deposit> userDeposits = user.getDeposits();

        if (!userDeposits.isEmpty()) {
            log.info("Вклады пользователя с ID {} найдены.", id);
            List<DepositDto> userDepositsDtoList = new ArrayList<>();
            for (Deposit deposit : userDeposits) {
                DepositDto depositDto = convertDepositToDto(deposit);
                userDepositsDtoList.add(depositDto);
            }
            return userDepositsDtoList;
        } else {
            log.error("Вклады пользователя с ID {} не найдены.", id);
            throw new RuntimeException("Вклады пользователя с ID " + id + " не найдены.");
        }
    }

    @SneakyThrows
    @Override
    @Transactional
    public DepositDto updateDeposit(DepositDto depositDto) throws DataAccessException, FailedConvertToDtoException {
        log.info("Внесение изменений во вклад с ID: {}", depositDto.getId());
        Deposit deposit = depositRepository.findById(depositDto.getId())
                .orElseThrow(() -> {
                    log.warn("Вклад с таким ID не найден.");
                    return new DepositNotFoundException("Вклад с таким ID не найден.");
                });

        deposit.setStatusDeposit(DepositStatus.valueOf(depositDto.getStatusDeposit()));
        deposit.setAmountDeposit(depositDto.getAmountDeposit());
        deposit.setInterestRateDeposit(depositDto.getInterestRateDeposit());
        deposit.setStartDateDeposit(depositDto.getStartDateDeposit());
        deposit.setEndDateDeposit(depositDto.getEndDateDeposit());

        log.info("Вклад с ID {} успешно изменен.", depositDto.getId());
        depositRepository.save(deposit);
        return convertDepositToDto(deposit);
    }

    @SneakyThrows
    @Override
    public String deleteDeposit(Long id) throws Exception {
        log.info("Удаление вклада с ID: {}", id);
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вклад с таким ID не найден.");
                    return new DepositNotFoundException("Вклад с таким ID не найден.");
                });
            depositRepository.deleteById(id);
            log.info("Вклад с ID {} успешно удален.", id);
            return "Вклад с ID " + id + " был удален.";
    }

    private DepositDto convertDepositToDto(Deposit deposit) {
        log.debug("Трансформация вклада с ID {} в DTO.", deposit.getId());
        return DepositDto.builder()
                .id(deposit.getId())
                .account(convertAccountToDto(deposit.getAccount()))
                .user(convertUserToDto(deposit.getUser()))
                .amountDeposit(deposit.getAmountDeposit())
                .startDateDeposit(deposit.getStartDateDeposit())
                .endDateDeposit(deposit.getEndDateDeposit())
                .interestRateDeposit(deposit.getInterestRateDeposit())
                .statusDeposit(String.valueOf(deposit.getStatusDeposit()))
                .build();
    }

    private UserDto convertUserToDto(User user) {
        log.debug("Трансформация пользователя с ID {} в DTO.", user.getId());
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
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
