package com.carrefour.infrastructure.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.List;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (!jwtService.validateToken(token)) {
            return Mono.empty();
        }

        String username = jwtService.extractUsername(token);

        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of() // pas de rôles pour le MVP
                )
        );
    }
}
