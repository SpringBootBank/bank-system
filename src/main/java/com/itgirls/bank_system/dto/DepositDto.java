package com.itgirls.bank_system.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositDto {

    private long id;

    @NotNull(message = "Укажите сумму депозита.")
    @Positive(message = "Сумма депозита должна быть больше 0.")
    private BigDecimal amountDeposit;

    @NotNull(message = "Укажите процентную ставку.")
    @Positive(message = "Процентная ставка должна быть больше 0%.")
    @Digits(integer = 3, fraction = 2, message = "Процентная ставка может содержать максимум 3 цифры до запятой и 2 после.")
    private BigDecimal interestRateDeposit;

    @NotNull(message = "Введите дату начала депозита.")
    private LocalDate startDateDeposit;

    @NotNull(message = "Введите дату окончания депозита.")
    private LocalDate endDateDeposit;

    @NotNull
    @Pattern(regexp = "^(ACTIVE|CLOSED|FROZEN)$", message = "Статус депозита должен быть ACTIVE, CLOSED или FROZEN.")
    private String statusDeposit;

    @NotNull(message = "Введите ID депозитного счёта.")
    private AccountDto account;

    private UserDto user;
}