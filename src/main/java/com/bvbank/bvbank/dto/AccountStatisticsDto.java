package com.bvbank.bvbank.dto;

public record AccountStatisticsDto(
        long highBalanceAccounts,
        long mediumBalanceAccounts,
        long lowBalanceAccounts,
        long totalTransactions
) {}
