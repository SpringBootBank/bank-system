package com.itgirls.bank_system.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
public class LoanDto {
    private Long id;

    @NotNull(message = "Укажите сумму кредита.")
    @Positive(message = "Сумма кредита должна быть положительной.")
    private BigDecimal amountLoan;

    @NotNull(message = "Укажите процентную ставку.")
    @Positive(message = "Процентная ставка должна быть положительной.")
    @Digits(integer = 3, fraction = 2, message = "Процентная ставка может содержать максимум 3 цифры до запятой и 2 после.")
    private BigDecimal interestRateLoan;

    @NotNull(message = "Введите дату начала кредита.")
    private LocalDate startDateLoan;

    @NotNull(message = "Введите дату окончания кредита.")
    private LocalDate endDateLoan;

    @NotNull(message = "Укажите сумму ежемесячного платежа по кредиту.")
    @Positive(message = "Сумма ежемесячного платежа по кредиту должна быть положительной.")
    private BigDecimal monthlyPayment;

    @NotNull
    @Pattern(regexp = "^(ACTIVE|CLOSED|OVERDUE)$", message = "Статус кредита должен быть ACTIVE, CLOSED или OVERDUE.")
    private String statusLoan;

    @NotNull(message = "Укажите счет, к которому оформлен кредит.")
    private Long accountId;
    private String accountNumber;

    @NotNull(message = "Укажите держателя кредита.")
    private Long userId;
    private String userName;
    private String userSurname;
}