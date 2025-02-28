package com.itgirls.bank_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {

    private Long id;

    @NotNull(message = "Номер счета обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер счета должен содержать 16 цифр")
    private String accountNumber;

    @NotNull(message = "Баланс обязателен")
    @Min(value = 0, message = "Баланс не может быть отрицательным")
    private BigDecimal balance;

    @NotNull(message = "Тип счета обязателен")
    private String type;
}
