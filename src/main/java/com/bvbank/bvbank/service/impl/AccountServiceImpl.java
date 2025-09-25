package com.bvbank.bvbank.service.impl;

import com.bvbank.bvbank.model.Account;
import com.bvbank.bvbank.model.Customer;
import com.bvbank.bvbank.repository.AccountRepository;
import com.bvbank.bvbank.repository.CustomerRepository;
import com.bvbank.bvbank.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> getAccountsByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
        return accountRepository.findByCustomer(customer);
    }

    @Override
    public Account createAccount(Account account) {
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new RuntimeException("Account number already exists: " + account.getAccountNumber());
        }
        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(Long id, Account updatedAccount) {
        return accountRepository.findById(id)
                .map(existing -> {
                    existing.setAccountNumber(updatedAccount.getAccountNumber());
                    existing.setBalance(updatedAccount.getBalance());
                    existing.setLimitAmount(updatedAccount.getLimitAmount());
                    existing.setOpenedDate(updatedAccount.getOpenedDate());
                    existing.setCustomer(updatedAccount.getCustomer());
                    return accountRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found with id " + id);
        }
        accountRepository.deleteById(id);
    }
}
