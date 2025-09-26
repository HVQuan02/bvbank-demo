package com.bvbank.bvbank.service;

import com.bvbank.bvbank.dto.AccountStatisticsDto;
import com.bvbank.bvbank.dto.CustomerLocationCountDto;

import java.util.List;

public interface StatisticsService {
    AccountStatisticsDto getAccountAndTransactionStatistics();
    List<CustomerLocationCountDto> getCustomerCountByLocation();
}
