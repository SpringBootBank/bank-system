package com.itgirls.bank_system.specification;

import com.itgirls.bank_system.model.Transactions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class TransactionSpecification {
    public static Specification<Transactions> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("transactionType"), type);
    }

    public static Specification<Transactions> hasSenderId(Long senderId) {
        return (root, query, criteriaBuilder) -> {
            log.info("Фильтр по senderAccount.id: {}", senderId);
            return criteriaBuilder.equal(root.get("senderAccount").get("id"), senderId);
        };
    }

       public static Specification<Transactions> hasBeneficiaryId(Long beneficiaryId) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("beneficiaryAccount").get("id"), beneficiaryId);
        }
    }
