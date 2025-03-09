package com.itgirls.bank_system.specification;

import com.itgirls.bank_system.enums.AccountType;
import com.itgirls.bank_system.model.Account;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountSpecification implements Specification<Account> {
    private final String accountNumber;
    private final BigDecimal minBalance;
    private final BigDecimal maxBalance;
    private final String accountType;

    public AccountSpecification(String accountNumber, BigDecimal minBalance, BigDecimal maxBalance, String accountType) {
        this.accountNumber = accountNumber;
        this.minBalance = minBalance;
        this.maxBalance = maxBalance;
        this.accountType = accountType;
    }

    @Override
    public Predicate toPredicate(Root<Account> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (accountNumber != null) {
            predicates.add(criteriaBuilder.equal(root.get("accountNumber"), accountNumber));
        }
        if (minBalance != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), minBalance));
        }
        if (maxBalance != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("balance"), maxBalance));
        }
        if (accountType != null) {
            predicates.add(criteriaBuilder.equal(root.get("type"), AccountType.valueOf(accountType.toUpperCase())));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}