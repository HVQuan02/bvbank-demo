package com.bvbank.bvbank.service.impl;

import com.bvbank.bvbank.dto.AccountStatisticsDto;
import com.bvbank.bvbank.dto.CustomerLocationCountDto;
import com.bvbank.bvbank.model.Account;
import com.bvbank.bvbank.repository.AccountRepository;
import com.bvbank.bvbank.repository.TransactionRepository;
import com.bvbank.bvbank.repository.CustomerRepository;
import com.bvbank.bvbank.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public StatisticsServiceImpl(AccountRepository accountRepository,
                                 TransactionRepository transactionRepository,
                                 CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public AccountStatisticsDto getAccountAndTransactionStatistics() {
        List<Account> accounts = accountRepository.findAll();

        long high = accounts.stream()
                .filter(a -> a.getBalance().compareTo(new BigDecimal("100000000")) >= 0)
                .count();

        long medium = accounts.stream()
                .filter(a -> a.getBalance().compareTo(new BigDecimal("10000000")) >= 0
                        && a.getBalance().compareTo(new BigDecimal("100000000")) < 0)
                .count();

        long low = accounts.stream()
                .filter(a -> a.getBalance().compareTo(new BigDecimal("10000000")) < 0)
                .count();

        long txCount = transactionRepository.count();

        return new AccountStatisticsDto(high, medium, low, txCount);
    }

    
    @Override
    public List<CustomerLocationCountDto> getCustomerCountByLocation() {
        return customerRepository.countCustomersByLocation();
    }
}
