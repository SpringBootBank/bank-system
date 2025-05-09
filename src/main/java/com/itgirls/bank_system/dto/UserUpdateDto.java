package com.itgirls.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itgirls.bank_system.enums.Role;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.Loan;
import com.itgirls.bank_system.model.Transactions;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateDto {

    @NotNull
    private Long id;

    @Size(min = 3, max = 25)
    private String name;

    @Size(min = 3, max = 25)
    private String surname;

    @Email
    private String email;

    @Size(min = 5, max = 50)
    private String password;

    private Role role;

    private Set<Account> accounts;

    private Set<Deposit> deposits;

    private Set<Loan> loans;

    private Set<Transactions> transactions;
}