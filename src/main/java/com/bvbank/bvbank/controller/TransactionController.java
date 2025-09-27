package com.bvbank.bvbank.controller;

import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.model.TransactionType;
import com.bvbank.bvbank.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isRelatedToTransaction(#id, authentication.name)")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelfAccount(#accountId, authentication.name)")
    public ResponseEntity<List<Transaction>> getByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction created = transactionService.createTransaction(transaction);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/{accountId}/search")
@PreAuthorize("hasRole('ADMIN') or @authz.isSelfAccount(#accountId, authentication.name)")
public ResponseEntity<Page<Transaction>> searchTransactions(
        @PathVariable Long accountId,
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) BigDecimal minAmount,
        @RequestParam(required = false) BigDecimal maxAmount,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "transactionDate,desc") String sort
) {
    String[] parts = sort.split(",");
Sort sortObj = Sort.by(Sort.Direction.fromString(parts.length > 1 ? parts[1] : "desc"), parts[0]);
    Pageable pageable = PageRequest.of(page, size, sortObj);

    Page<Transaction> result = transactionService.searchTransactions(
            accountId, type, minAmount, maxAmount, fromDate, toDate, pageable
    );
    return ResponseEntity.ok(result);
}

}
