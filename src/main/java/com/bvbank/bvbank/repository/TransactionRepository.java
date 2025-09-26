package com.bvbank.bvbank.repository;

import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount);
}
