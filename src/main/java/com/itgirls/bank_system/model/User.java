package com.itgirls.bank_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.itgirls.bank_system.enums.Role;

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

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "surname", nullable = false)
    private String surname;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Setter
    @OneToMany(mappedBy = "user")
    private Set <Account> accounts;

}