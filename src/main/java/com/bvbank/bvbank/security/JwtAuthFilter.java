package com.bvbank.bvbank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
protected void doFilterInternal(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull FilterChain filterChain) throws ServletException, IOException {
    String token = extractJwtFromCookies(request);

    if (token != null) {
        try {
            Jws<Claims> claims = jwtUtil.validateToken(token);
            String username = claims.getBody().getSubject();
            String role = claims.getBody().get("role", String.class);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
        }
    }

    filterChain.doFilter(request, response);
}

    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("jwt")) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
