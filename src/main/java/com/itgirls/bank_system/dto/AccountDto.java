package com.itgirls.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {

    private Long id;

    @NotBlank(message = "Номер счета обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер счета должен содержать 16 цифр")
    private String accountNumber;

    @NotBlank(message = "Баланс обязателен")
    @Min(value = 0, message = "Баланс не может быть отрицательным")
    private BigDecimal balance;

    @NotBlank(message = "Тип счета обязателен")
    private String type;
}
