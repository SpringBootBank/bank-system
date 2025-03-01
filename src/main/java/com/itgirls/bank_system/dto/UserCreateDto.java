package com.itgirls.bank_system.dto;

import com.itgirls.bank_system.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCreateDto {

    @Size(min = 3, max = 25)
    @NotBlank(message = "Введите имя пользователя")
    private String name;

    @Size(min = 3, max = 25)
    @NotBlank(message = "Введите фамилию пользователя")
    private String surname;

    @Email
    @NotBlank(message = "Введите адрес электронной почты пользователя")
    private String email;

    @Size (min = 5, max = 50)
    @NotBlank (message = "Установите пароль пользователя")
    private String password;

    @NotBlank(message = "Укажите роль пользователя")
    private Role role;
}
