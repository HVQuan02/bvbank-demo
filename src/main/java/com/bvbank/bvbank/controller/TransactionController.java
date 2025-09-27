package com.bvbank.bvbank.controller;

import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
