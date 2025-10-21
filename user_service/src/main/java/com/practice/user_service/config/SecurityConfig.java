package com.practice.user_service.config;

import com.practice.user_service.security.GatewayIdentityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Example: only ADMIN can delete users
                        .requestMatchers("/api/users/delete/**").hasRole("ADMIN")
                        // Everyone (user or admin) can view or list users
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                // Add the custom GatewayIdentityFilter before the built-in UsernamePasswordAuthenticationFilter
                .addFilterBefore(new GatewayIdentityFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
