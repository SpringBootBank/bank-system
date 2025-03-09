package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.UserCreateDto;
import com.itgirls.bank_system.dto.UserDto;
import com.itgirls.bank_system.dto.UserUpdateDto;
import com.itgirls.bank_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление пользователями")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создание пользователя", description = "Метод позволяет создать нового пользователя")
    public ResponseEntity<?> addNewUser(@RequestBody @Valid UserCreateDto user) {
        try {
            return ResponseEntity.ok().body(userService.addNewUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Все пользователи", description = "Метод позволяет получить список всех пользователей с их счетами")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск пользователя", description = "Метод позволяет найти пользователя с заданным id")
    public ResponseEntity<?> getUserByID(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(userService.getUserByID(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "Изменение пользователя целиком", description = "Метод изменяет все поля пользователя")
    public ResponseEntity<?> updateUserPut(@RequestBody @Valid UserUpdateDto user) {
        try {
            return ResponseEntity.ok().body(userService.updateUser(user, false));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    @Operation(summary = "Изменение отдельных полей пользователя", description = "Метод изменяет заданные поля пользователя")
    public ResponseEntity<?> updateUserPatch(@RequestBody @Valid UserUpdateDto user) {
        try {
            return ResponseEntity.ok().body(userService.updateUser(user, true));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя", description = "Метод позволяет удалить пользователя по идентификатору")
    ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        try {
            return userService.deleteUser(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}