package com.bvbank.bvbank.service.impl;

import com.bvbank.bvbank.model.Account;
import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.model.TransactionType;
import com.bvbank.bvbank.repository.AccountRepository;
import com.bvbank.bvbank.repository.TransactionRepository;
import com.bvbank.bvbank.service.TransactionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Account fromAcc = null;
        if (transaction.getFromAccount() != null) {
            fromAcc = accountRepository.findById(transaction.getFromAccount().getId())
                    .orElseThrow(() -> new RuntimeException("From account not found"));
        }

        Account toAcc = null;
        if (transaction.getToAccount() != null) {
            toAcc = accountRepository.findById(transaction.getToAccount().getId())
                    .orElseThrow(() -> new RuntimeException("To account not found"));
        }

        BigDecimal amount = transaction.getAmount();
        TransactionType type = transaction.getTransactionType();

        if (type == TransactionType.WITHDRAW || type == TransactionType.TRANSFER) {
            if (fromAcc == null) {
                throw new RuntimeException("From account is required for this transaction");
            }
            if (fromAcc.getLimitAmount() != null && amount.compareTo(fromAcc.getLimitAmount()) > 0) {
                throw new RuntimeException("Transaction amount exceeds account limit");
            }
            if (fromAcc.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
        }

        switch (type) {
          case DEPOSIT -> {
              if (toAcc == null) throw new RuntimeException("To account is required for deposit");
              toAcc.setBalance(toAcc.getBalance().add(amount));
              accountRepository.save(toAcc);
          }
          case WITHDRAW -> {
              if (fromAcc == null) throw new RuntimeException("From account is required for withdraw");
              fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
              accountRepository.save(fromAcc);
          }
          case TRANSFER -> {
              if (fromAcc == null) throw new RuntimeException("From account is required for transfer");
              if (toAcc == null) throw new RuntimeException("To account is required for transfer");
              fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
              toAcc.setBalance(toAcc.getBalance().add(amount));
              accountRepository.save(fromAcc);
              accountRepository.save(toAcc);
          }
      }      

        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
public Long getTransactionCustomerId(Long transactionId) {
    return transactionRepository.findById(transactionId)
            .map(tx -> {
                if (tx.getFromAccount() != null) return tx.getFromAccount().getCustomer().getId();
                if (tx.getToAccount() != null) return tx.getToAccount().getCustomer().getId();
                return null;
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
}

@Override
public List<Transaction> getTransactionsByAccountId(Long accountId) {
    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    return transactionRepository.findByFromAccountOrToAccount(account, account);
}

}
