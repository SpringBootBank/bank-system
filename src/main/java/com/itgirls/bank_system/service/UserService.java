package com.itgirls.bank_system.service;

import com.itgirls.bank_system.dto.UserCreateDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.dto.UserUpdateDto;
import com.itgirls.bank_system.model.User;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserCreateDto userCreateDto);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserUpdateDto userUpdateDto);

    UserDto getUserByID(Long id);

    void deleteUser(Long id);
}
