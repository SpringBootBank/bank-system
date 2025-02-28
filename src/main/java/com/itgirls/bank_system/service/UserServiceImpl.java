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
    public List<UserDto> getAllUsers () {
        return userRepository.findAll().stream().map(this::convertUserToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto) {
        User newUser = userRepository.findUserById(userUpdateDto.getId());
        String passwordEncode = passwordEncoder.encode(userUpdateDto.getPassword());
        newUser.setPassword(passwordEncode);
        newUser.setRole(userUpdateDto.getRole());
        newUser.setName(userUpdateDto.getName());
        newUser.setSurname(userUpdateDto.getSurname());
        newUser.setEmail(userUpdateDto.getEmail());
        newUser.setAccounts(userUpdateDto.getAccounts());
        userRepository.save(newUser);
        return convertUserToDto(newUser);
    }

    @Override
    public UserDto getUserByID(Long id) {
        User foundUser = userRepository.findUserById(id);
        return convertUserToDto(foundUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto convertUserToDto (User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .accounts(user.getAccounts())
                .build();
    }
}
