package com.bvbank.bvbank.controller;

import com.bvbank.bvbank.model.Customer;
import com.bvbank.bvbank.model.Role;
import com.bvbank.bvbank.model.User;
import com.bvbank.bvbank.repository.CustomerRepository;
import com.bvbank.bvbank.repository.UserRepository;
import com.bvbank.bvbank.security.JwtUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          CustomerRepository customerRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("Login successful");
    }

    @PostMapping("/signup")
public ResponseEntity<?> signup(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam String address) {
    if (userRepository.findByUsername(username).isPresent()) {
        return ResponseEntity.badRequest().body("Username already exists");
    }

    Customer customer = Customer.builder()
            .name(name)
            .email(email)
            .phone(phone)
            .address(address)
            .build();

    customer = customerRepository.save(customer);

    User newUser = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(Role.CUSTOMER)
            .customer(customer)
            .build();

    userRepository.save(newUser);

    return ResponseEntity.ok("User registered successfully");
}

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body("Logout successful");
    }


}
