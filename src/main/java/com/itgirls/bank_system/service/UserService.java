package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.UserCreateDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.dto.UserUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserCreateDto userCreateDto);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserUpdateDto userUpdateDto, boolean usePatch);

    UserDto getUserByID(Long id);

    ResponseEntity<String> deleteUser(Long id);
}