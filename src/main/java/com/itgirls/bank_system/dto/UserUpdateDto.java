package com.itgirls.bank_system.dto;

import com.itgirls.bank_system.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdateDto {

    @NotBlank(message = "Введите id пользователя")
    private Long id;

    @Size(min = 3, max = 25)
    private String name;

    @Size(min = 3, max = 25)
    private String surname;

    @Email
    private String email;

    @Size (min = 5, max = 50)
    private String password;

    private Role role;

    private Set <Account> accounts;
}
