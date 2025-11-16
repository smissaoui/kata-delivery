package com.carrefour.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationManager jwtAuthenticationManager(JwtService jwtService) {
        return new JwtAuthenticationManager(jwtService);
    }

    @Bean
    public AuthenticationWebFilter jwtAuthFilter(JwtAuthenticationManager manager) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);
        filter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter());
        filter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return filter;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http,
                                              AuthenticationWebFilter jwtFilter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)

                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs/swagger-config",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/auth/login"
                        ).permitAll()
                        .anyExchange().authenticated()
                )

                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}