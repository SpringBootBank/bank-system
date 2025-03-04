package com.itgirls.bank_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.itgirls.bank_system.enums.Role;

import java.util.Set;

@Entity
@Table(name = "bank_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", @setNull, fetch = FetchType.LAZY)
    private Set<Account> accounts;

    @OneToMany(mappedBy = "user",  @setNull, fetch = FetchType.LAZY)
    private Set<Loan> loans;

    @OneToMany(mappedBy = "userDeposit", @setNull, fetch = FetchType.LAZY)
    private Set<Deposit> deposits;

    @OneToMany(mappedBy = "user", @setNull, fetch = FetchType.LAZY)
    private Set<Transactions> transactions;
}