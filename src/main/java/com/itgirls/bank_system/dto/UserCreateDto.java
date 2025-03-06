package com.itgirls.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itgirls.bank_system.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateDto {

    @Size(min = 3, max = 25)
    @NotNull
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

    @NotNull
    private Role role;
}
