package com.itgirls.bank_system.entity;

import com.itgirls.bank_system.enums.CardStatus;
import com.itgirls.bank_system.enums.CardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", length = 16, unique = true)
    @NotBlank
    @Size(min = 16, max = 16, message = "В номере карты должно быть 16 цифр.")
    private String cardNumber;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(length = 3)
    @NotBlank
    @Size(min = 3, max = 3, message = "Cvv код состоит из 3-х цифр.")
    private String cvv;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", length = 20, nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CardStatus status;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

}
