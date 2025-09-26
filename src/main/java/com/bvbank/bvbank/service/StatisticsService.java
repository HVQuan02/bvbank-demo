package com.bvbank.bvbank.service;

import com.bvbank.bvbank.dto.AccountStatisticsDto;

public interface StatisticsService {
    AccountStatisticsDto getAccountAndTransactionStatistics();
}
