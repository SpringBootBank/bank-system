package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.DepositDto;
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

import static com.itgirls.bank_system.mapper.EntityToDtoMapper.convertDepositToDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public DepositDto createDeposit(DepositDto depositDto, Authentication authentication)
            throws DataAccessException, FailedConvertToDtoException, UserNotFoundException {
        log.info("Открытие нового вклада.");
        Account account = accountRepository.findById(depositDto.getAccount().getId())
                .orElseThrow(() -> {
                    log.warn("Счет с таким ID не найден.");
                    throw new NoSuchElementException("Счет с таким ID не найден.");
                });

        User user;
        try {
            user = userRepository.findUserByEmail(authentication.getName());
        } catch (NoSuchElementException e) {
            log.warn("Пользователь с таким ID не найден.");
            throw new UserNotFoundException("Пользователь с таким ID не найден.");
        }

        Deposit newDeposit = new Deposit();

        if (user.getRole() == Role.ADMIN) {
            System.out.println("Введите пользователя, на имя которого открывается вклад.");
            throw new IllegalArgumentException("При создании вклада админом нужно " +
                    "указать пользователя на имя которого открывается вклад.");
        } else if (!user.getId().equals(account.getUser().getId())) {
            throw new IllegalStateException("Этот счет привязан к другому пользователю.");
        } else if (account.getDeposit() != null) {
            throw new IllegalStateException("Этот счет уже привязан к другому вкладу.");
        } else {
            newDeposit.setAmountDeposit(depositDto.getAmountDeposit());
            newDeposit.setInterestRateDeposit(depositDto.getInterestRateDeposit());
            newDeposit.setStartDateDeposit(depositDto.getStartDateDeposit());
            newDeposit.setEndDateDeposit(depositDto.getEndDateDeposit());
            newDeposit.setStatusDeposit(DepositStatus.valueOf(depositDto.getStatusDeposit()));
            newDeposit.setAccount(account);
            newDeposit.setUser(user);

            log.info("Сохранение нового вклада");
            depositRepository.save(newDeposit);
            log.info("Новый вклад с ID: {} успешно сохранен.", newDeposit.getId());
            return convertDepositToDto(newDeposit);
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

    @Override
    public DepositDto getDepositById(Long id) throws FailedConvertToDtoException, DepositNotFoundException {
        log.info("Поиск вклада по ID.");
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вклад с таким ID не найден.");
                    return new DepositNotFoundException("Вклад с таким ID не найден.");
                });
        log.info("Вклад с ID {} найден.", id);
        return convertDepositToDto(deposit);
    }

    public List<DepositDto> getDepositsByUserId(Long id) throws UserNotFoundException {
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

    @Override
    @Transactional
    public DepositDto updateDeposit(DepositDto depositDto, Authentication authentication)
            throws DataAccessException, DepositNotFoundException, UserNotFoundException, FailedConvertToDtoException {

        Deposit deposit = depositRepository.findById(depositDto.getId())
                .orElseThrow(() -> {
                    log.warn("Вклад с таким ID не найден.");
                    return new DepositNotFoundException("Вклад с таким ID не найден.");
                });

        User user;
        try {
            user = userRepository.findUserByEmail(authentication.getName());
        } catch (NoSuchElementException e) {
            log.warn("Пользователь с таким ID не найден.");
            throw new UserNotFoundException("Пользователь с таким ID не найден.");
        }

        if (user.getRole() != Role.ADMIN || !user.getId().equals(deposit.getUser().getId())) {
            throw new IllegalStateException("У вас нет доступа к изменению этого вклада.");
        } else {
            log.info("Внесение изменений во вклад с ID: {}", depositDto.getId());
            deposit.setStatusDeposit(DepositStatus.valueOf(depositDto.getStatusDeposit()));
            deposit.setAmountDeposit(depositDto.getAmountDeposit());
            deposit.setInterestRateDeposit(depositDto.getInterestRateDeposit());
            deposit.setStartDateDeposit(depositDto.getStartDateDeposit());
            deposit.setEndDateDeposit(depositDto.getEndDateDeposit());

            log.info("Вклад с ID {} успешно изменен.", depositDto.getId());
            depositRepository.save(deposit);
            log.info("Изменения успешно сохранены.");
            return convertDepositToDto(deposit);
        }
    }

    @Override
    public String deleteDeposit(Long id, Authentication authentication)
            throws DepositNotFoundException, UserNotFoundException {
        log.info("Удаление вклада с ID: {}", id);
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вклад с таким ID не найден.");
                    return new DepositNotFoundException("Вклад с таким ID не найден.");
                });

        User user;
        try {
            user = userRepository.findUserByEmail(authentication.getName());
        } catch (NoSuchElementException e) {
            log.warn("Пользователь с таким ID не найден.");
            throw new UserNotFoundException("Пользователь с таким ID не найден.");
        }

        if (user.getRole() != Role.ADMIN || !user.getId().equals(deposit.getUser().getId())) {
            throw new IllegalStateException("У вас нет доступа к изменению этого вклада.");
        } else {
            int depositsBeforeDelete = Math.toIntExact(depositRepository.count());
            log.info("Кол-во вкладов до удаления: {}", depositsBeforeDelete);

            deposit.getUser().getDeposits().remove(deposit);
            deposit.getAccount().setDeposit(null);
            depositRepository.deleteById(id);
            depositRepository.flush();

            int depositsAfterDelete = Math.toIntExact(depositRepository.count());
            log.info("Кол-во вкладов после удаления: {}", depositsAfterDelete);

            if (depositsBeforeDelete == depositsAfterDelete) {
                log.error("Не удалось удалить вклад с ID {} из БД.", id);
                return "Не удалось удалить вклад с ID " + id + " из БД.";
            } else {
                log.info("Вклад с ID {} успешно удален.", id);
                return "Вклад с ID " + id + " был удален.";
            }
        }
    }
}
