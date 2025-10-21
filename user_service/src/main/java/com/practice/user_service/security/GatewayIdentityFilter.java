package com.practice.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * GatewayIdentityFilter
 * <p>
 * This filter reads trusted user information (user ID, role, email)
 * from request headers that were injected by the API Gateway
 * *after* successful authentication.
 * <p>
 * Its job is to create a Spring Security authentication context
 * based on these headers, so that downstream services can perform
 * authorization checks (e.g., @PreAuthorize("hasRole('ADMIN')"))
 * without needing to re-validate JWTs.
 */
public class GatewayIdentityFilter extends OncePerRequestFilter {

    // Header names should match exactly what your Gateway sets
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_EMAIL = "X-User-Email";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ Extract identity details from headers
        String userId = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_USER_ROLE);
        String email = request.getHeader(HEADER_USER_EMAIL);

        // 2️⃣ Proceed only if the Gateway added these headers
        if (userId != null && role != null && email != null) {

            // Create a simple "GrantedAuthority" from the role
            // Spring expects roles in format: ROLE_ADMIN, ROLE_USER
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role.toUpperCase());

            // Create an Authentication object (no password required)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

            // Optionally store extra details for auditing or logging
            authentication.setDetails(userId);

            // 3️⃣ Set the authentication into the Spring Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 4️⃣ Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
