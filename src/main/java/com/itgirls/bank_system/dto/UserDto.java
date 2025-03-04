package com.itgirls.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itgirls.bank_system.model.Account;
import com.itgirls.bank_system.model.Deposit;
import com.itgirls.bank_system.model.Loan;
import com.itgirls.bank_system.model.Transactions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.itgirls.bank_system.enums.Role;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private String surname;

    private Role role;

    private Set<Account> accounts;

    private Set<Deposit> deposits;

    private Set<Loan> loans;

    private Set <Transactions> transactions;
}
