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
    UserDto getUserByID(@PathVariable("id") Long id) {
        return userService.getUserByID(id);
    }

    @PutMapping
    public UserDto updateUserPut(@RequestBody @Valid UserUpdateDto user) {
        return userService.updateUser(user, false);
    }

    @PatchMapping
    public UserDto updateUserPatch (@RequestBody @Valid UserUpdateDto user) {
        return userService.updateUser(user, true);
    }

    @DeleteMapping("/{id}")
    String deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }
}
