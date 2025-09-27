package com.bvbank.bvbank.config;

import com.bvbank.bvbank.model.Role;
import com.bvbank.bvbank.model.User;
import com.bvbank.bvbank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
