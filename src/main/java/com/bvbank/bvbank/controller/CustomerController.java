package com.bvbank.bvbank.controller;

import com.bvbank.bvbank.model.Customer;
import com.bvbank.bvbank.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelfCustomer(#id, authentication.name)")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelfCustomer(#id, authentication.name)")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @RequestBody Customer customer) {
        try {
            Customer updated = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
