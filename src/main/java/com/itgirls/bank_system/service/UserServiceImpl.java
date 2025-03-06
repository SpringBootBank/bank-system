package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.UserCreateDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.dto.UserUpdateDto;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto addNewUser(UserCreateDto userCreateDto) {
        String passwordEncode = passwordEncoder.encode(userCreateDto.getPassword());
        User user = new User();
        user.setName(userCreateDto.getName());
        user.setSurname((userCreateDto.getSurname()));
        user.setEmail(userCreateDto.getEmail());
        user.setRole(userCreateDto.getRole());
        user.setPassword(passwordEncode);
        userRepository.save(user);
        return convertUserToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertUserToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto, boolean usePatch) {
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
        return convertUserToDto(user);
    }

    @Override
    public UserDto getUserByID(Long id) {
        User foundUser = userRepository.findUserById(id);
        return convertUserToDto(foundUser);
    }

    @Override
    public String deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            try {
                userRepository.deleteById(id);
                return "Пользователь с идентификаторо м" + id + " успешно удален";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else return "Пользователь с идентификатором " + id + " не найден";
    }

    private UserDto convertUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .accounts(user.getAccounts())
                .deposits(user.getDeposits())
                .loans(user.getLoans())
                .transactions(user.getTransactions())
                .build();
    }
}