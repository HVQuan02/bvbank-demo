package com.bvbank.bvbank.security;

import com.bvbank.bvbank.model.User;
import com.bvbank.bvbank.model.Transaction;
import com.bvbank.bvbank.repository.AccountRepository;
import com.bvbank.bvbank.repository.TransactionRepository;
import com.bvbank.bvbank.repository.UserRepository;

import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    public AuthorizationService(UserRepository userRepository, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public boolean isSelfCustomer(Long customerId, String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        return user != null &&
               user.getCustomer() != null &&
               user.getCustomer().getId().equals(customerId);
    }

    public boolean isSelfUser(Long userId, String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        return user != null && user.getId().equals(userId);
    }

    public boolean isSelfAccount(Long accountId, String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || user.getCustomer() == null) return false;
    
        return accountRepository.findById(accountId)
                .map(acc -> acc.getCustomer().getId().equals(user.getCustomer().getId()))
                .orElse(false);
    }     

    public boolean isRelatedToTransaction(Long transactionId, String username) {
      User user = userRepository.findByUsername(username).orElse(null);
      if (user == null || user.getCustomer() == null) return false;
      Long custId = user.getCustomer().getId();
      Transaction tx = transactionRepository.findById(transactionId).orElse(null);
      if (tx == null) return false;
      if (tx.getFromAccount() != null && tx.getFromAccount().getCustomer() != null
              && custId.equals(tx.getFromAccount().getCustomer().getId())) return true;
      if (tx.getToAccount() != null && tx.getToAccount().getCustomer() != null
              && custId.equals(tx.getToAccount().getCustomer().getId())) return true;
      return false;
  }
}
