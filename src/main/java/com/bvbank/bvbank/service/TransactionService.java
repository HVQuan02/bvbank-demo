package com.bvbank.bvbank.service;

import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    Optional<Transaction> getTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    void deleteTransaction(Long id);
    Long getTransactionCustomerId(Long transactionId);
List<Transaction> getTransactionsByAccountId(Long accountId);
Page<Transaction> searchTransactions(Long accountId,
                                     TransactionType type,
                                     BigDecimal minAmount,
                                     BigDecimal maxAmount,
                                     LocalDateTime fromDate,
                                     LocalDateTime toDate,
                                     Pageable pageable);

}
