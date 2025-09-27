package com.bvbank.bvbank.service.impl;

import com.bvbank.bvbank.model.User;
import com.bvbank.bvbank.repository.UserRepository;
import com.bvbank.bvbank.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(updated.getUsername());
                    if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(updated.getPassword()));
                    }
                    existing.setRole(updated.getRole());
                    existing.setCustomer(updated.getCustomer());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
