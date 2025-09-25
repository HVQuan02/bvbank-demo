package com.bvbank.bvbank.service;

import com.bvbank.bvbank.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> getAllAccounts();
    Optional<Account> getAccountById(Long id);
    Optional<Account> getAccountByNumber(String accountNumber);
    List<Account> getAccountsByCustomerId(Long customerId);
    Account createAccount(Account account);
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id);
}
