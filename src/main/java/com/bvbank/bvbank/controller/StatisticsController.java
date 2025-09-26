package com.bvbank.bvbank.controller;

import com.bvbank.bvbank.dto.AccountStatisticsDto;
import com.bvbank.bvbank.dto.CustomerLocationCountDto;
import com.bvbank.bvbank.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/accounts-transactions")
    public ResponseEntity<AccountStatisticsDto> getAccountAndTransactionStatistics() {
        return ResponseEntity.ok(statisticsService.getAccountAndTransactionStatistics());
    }

    @GetMapping("/customers-by-location")
    public ResponseEntity<List<CustomerLocationCountDto>> getCustomerCountByLocation() {
        return ResponseEntity.ok(statisticsService.getCustomerCountByLocation());
    }

}
