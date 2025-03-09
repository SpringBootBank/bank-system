package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.*;
import com.itgirls.bank_system.mapper.EntityToDtoMapper;
import com.itgirls.bank_system.model.*;
import com.itgirls.bank_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto addNewUser(UserCreateDto userCreateDto) {
 log.info("Создание нового пользователя");
       if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            log.error("Пользователь с адресом {} уже зарегистрирован", userCreateDto.getEmail());
            throw new RuntimeException("Пользователь с таким email уже зарегисрирован");
        }
        try {      
            String passwordEncode = passwordEncoder.encode(userCreateDto.getPassword());
            User user = new User();
            user.setName(userCreateDto.getName());
            user.setSurname((userCreateDto.getSurname()));
            user.setEmail(userCreateDto.getEmail());
            user.setRole(userCreateDto.getRole());
            user.setPassword(passwordEncode);
            userRepository.save(user);
            log.info("Пользователь с email {} был создан.", user.getEmail());
            return EntityToDtoMapper.convertNewUserToDto(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных при создании пользователя: {}", e.getMessage());
            throw new RuntimeException("Нарушение уникальности данных: " + e.getMessage(), e);
        } catch (ConstraintViolationException e) {
            log.error("Ошибка валидации данных при создании пользователя: {}", e.getMessage());
            throw new RuntimeException("Некорректные данные: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка аргументов: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при создании нового пользователя", e);
            throw new RuntimeException("Произошла ошибка при создании пользователя", e);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
    log.info("Поиск всех пользователей");
      try {
        List<User> allUsers = userRepository.findAll();
        List<UserDto> allUsersDto = new ArrayList<>();
        for (User user : allUsers) {
            allUsersDto.add(EntityToDtoMapper.convertEveryUserToDto(user));
        }
        return allUsersDto;
        } catch (Exception e) {
            log.error("Ошибка поиска пользователей", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto, boolean usePatch) {
        log.info("Обновление сведений о пользователе с идентификатором {}", userUpdateDto.getId());
        try {
            Objects.requireNonNull(userUpdateDto, "UserUpdateDto не должен быть null");
            User user = userRepository.findById(userUpdateDto.getId()).
                    orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            if (!usePatch || userUpdateDto.getPassword() != null) {
                String passwordEncode = passwordEncoder.encode(userUpdateDto.getPassword());
                user.setPassword(passwordEncode);
            }
            if (!usePatch || userUpdateDto.getRole() != null) {
                user.setRole(userUpdateDto.getRole());
            }
            if (!usePatch || userUpdateDto.getName() != null) {
                user.setName(userUpdateDto.getName());
            }
            if (!usePatch || userUpdateDto.getSurname() != null) {
                user.setSurname(userUpdateDto.getSurname());
            }
            if (!usePatch || userUpdateDto.getEmail() != null) {
                user.setEmail(userUpdateDto.getEmail());
            }
            if (!usePatch && userUpdateDto.getAccounts() != null) {
                user.setAccounts(userUpdateDto.getAccounts());
            }
            if (!usePatch && userUpdateDto.getDeposits() != null) {
                user.setDeposits(userUpdateDto.getDeposits());
            }
            if (!usePatch && userUpdateDto.getLoans() != null) {
                user.setLoans(userUpdateDto.getLoans());
            }
            if (usePatch && userUpdateDto.getTransactions() != null) {
                user.setTransactions(userUpdateDto.getTransactions());
            }
            userRepository.save(user);
            log.info("Новые данные о пользователе с идентификатором {} сохранены", user.getId());
            return EntityToDtoMapper.convertEveryUserToDto(user);;
        } catch (EntityNotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка обновления пользователя", e);
            throw new RuntimeException("Ошибка обновления пользователя", e);
        }
    }

    @Override
    public UserDto getUserByID(Long id) {
    try {
        User foundUser = userRepository.findUserById(id);  
        log.info("Поиск пользователя с идентификатором {}", id);
        return EntityToDtoMapper.convertEveryUserToDto(foundUser);
        } catch (EntityNotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя", e);
            throw new RuntimeException("Ошибка при поиске пользователя", e);
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь к удалению не найден");
        }
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Пользователь удален успешно");
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении пользователя");
        }
    }
}