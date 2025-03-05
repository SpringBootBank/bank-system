package com.itgirls.bank_system.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itgirls.bank_system.model.Account;
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
    @FutureOrPresent(message = "Дата начала депозита должна быть не раньше сегодняшней.")
    private LocalDate startDateDeposit;

    @NotNull(message = "Введите дату окончания депозита.")
    @Future(message = "Дата окончания депозита должна быть позднее даты начала депозита.")
    private LocalDate endDateDeposit;

    @NotBlank
    @Pattern(regexp = "^(ACTIVE|CLOSED|FROZEN)$", message = "Статус депозита должен быть ACTIVE, CLOSED или FROZEN.")
    private String statusDeposit;

    @NotNull(message = "Введите ID депозитного счёта.")
    private Account account;

    @NotNull(message = "Введите ID пользователя, на имя которого оформлен вклад.")
    private UserDto user;
}
