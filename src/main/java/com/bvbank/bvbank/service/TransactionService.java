package com.bvbank.bvbank.service;

import com.bvbank.bvbank.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    Optional<Transaction> getTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    void deleteTransaction(Long id);
    Long getTransactionCustomerId(Long transactionId);
List<Transaction> getTransactionsByAccountId(Long accountId);

}
