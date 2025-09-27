package com.bvbank.bvbank.config;

import com.bvbank.bvbank.security.JwtAuthFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .accessDeniedHandler((req, res, e) -> res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/statistics/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/accounts/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/accounts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/accounts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/accounts/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/transactions/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/transactions/**").hasAnyRole("ADMIN","CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/transactions/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
