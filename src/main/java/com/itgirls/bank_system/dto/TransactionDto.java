package com.itgirls.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {

    private Long id;

    @Size(min = 5, message = "Номер транзакции должен содержать не менее 5 символов")
    private String transactionNumber;

    @NotNull(message = "Тип транзакции обязателен")
    @Pattern(regexp = "^(INCOMING|OUTGOING)$", message = "Тип транзакции должен быть:INCOMING или OUTGOING")
    private String transactionType;

    @NotNull(message = "Сумма транзакции обязательна")
    @DecimalMin(value = "0.01")
    private BigDecimal transactionAmount;

    private LocalDateTime transactionTime;

    @NotNull(message = "Указание отправителя обязательно")
    private Long senderAccountId;

    @NotNull(message = "Указание получателя  обязательно")
    private Long beneficiaryAccountId;

    @NotNull(message = "Указание пользователя обязательно")
    private Long bankUserId;
}