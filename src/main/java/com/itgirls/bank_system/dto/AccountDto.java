package com.itgirls.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {

    private Long id;

    @NotNull(message = "Номер счета не может быть пустым")
    @Pattern(regexp = "\\d{16}", message = "Номер счета должен содержать 16 цифр")
    private String accountNumber;

    @NotNull(message = "Баланс не может быть пустым")
    @Positive(message = "Баланс должен быть положительным числом")
    private BigDecimal balance;

    @NotBlank(message = "Тип счета обязателен")
    private String type;

    private Long userId;

    private Set<TransactionDto> outgoingTransactions;
    private Set<TransactionDto> incomingTransactions;
}