package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.UserCreateDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.dto.UserUpdateDto;
import com.itgirls.bank_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addNewUser(@RequestBody @Valid UserCreateDto user) {
        return ResponseEntity.ok(userService.addNewUser(user));
    }

    @GetMapping
    List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    UserDto getUserByID(@PathVariable Long id) {
        return userService.getUserByID(id);
    }

    @PutMapping
    public UserDto updateUser(@RequestBody @Valid UserUpdateDto user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
