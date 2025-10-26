package com.practice.gateway_service.filter;

import com.practice.gateway_service.dto.GatewayValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient webClient;
    private final String authServiceBaseUrl;

    public AuthenticationFilter(WebClient.Builder webClientBuilder,
                                @Value("${auth.service.base-url}") String authServiceBaseUrl) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
        this.authServiceBaseUrl = authServiceBaseUrl;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Allow login/register endpoints directly
            String path = exchange.getRequest().getURI().getPath();
            if (path.contains("/api/auth/") && (
                    path.endsWith("/login") ||
                            path.endsWith("/register") ||
                            path.endsWith("/refresh"))) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("❌ Missing or invalid Authorization header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            log.debug("Validating token for request path: {}", path);

            // Call the Auth Service /validate endpoint
            return webClient.get()
                    .uri(authServiceBaseUrl + "/api/auth/v1/gateway/validate")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        log.warn("❌ Auth Service returned error: {}", response.statusCode());
                        return Mono.error(new RuntimeException("Unauthorized"));
                    })
                    .bodyToMono(GatewayValidationResponse.class)
                    .flatMap(authResponse -> {
                        if (authResponse == null || !authResponse.getIsAuthenticated()) {
                            log.warn("❌ Authentication failed: invalid response from auth service");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        log.info("✅ Authenticated user: {} (role: {})",
                                authResponse.getUsername(), authResponse.getRole());

                        // Add user info to headers before forwarding
                        return chain.filter(
                                exchange.mutate()
                                        .request(r -> r.headers(headers -> {
                                            headers.add("X-User-Id", authResponse.getUserId());
                                            headers.add("X-User-Role", authResponse.getRole());
                                            headers.add("X-User-Email", authResponse.getEmail());
                                            headers.add("X-User-Name", authResponse.getUsername());
                                        }))
                                        .build()
                        );
                    })
                    .onErrorResume(e -> {
                        log.error("❌ Token validation failed: {}", e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    public static class Config {
        // can add future config options here
    }

}
