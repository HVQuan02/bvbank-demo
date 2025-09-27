package com.bvbank.bvbank.spec;

import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.model.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> hasAccountId(Long accountId) {
        return (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("fromAccount").get("id"), accountId),
                        cb.equal(root.get("toAccount").get("id"), accountId)
                );
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, cb) -> cb.equal(root.get("transactionType"), type);
    }

    public static Specification<Transaction> amountGreaterThanOrEqual(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), min);
    }

    public static Specification<Transaction> amountLessThanOrEqual(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), max);
    }

    public static Specification<Transaction> dateAfter(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), from);
    }

    public static Specification<Transaction> dateBefore(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), to);
    }
}
