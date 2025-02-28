package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserDto> addNewUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.addNewUser(user));
    }

    @GetMapping("/users")
    List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    UserDto getUserByID(@PathVariable Long id) {
        return userService.getUserByID(id);
    }

    @PutMapping("/users")
    public UserDto updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
