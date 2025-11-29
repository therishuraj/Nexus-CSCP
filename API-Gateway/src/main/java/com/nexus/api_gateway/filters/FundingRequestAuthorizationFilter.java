package com.nexus.api_gateway.filters;

import com.nexus.api_gateway.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * FundingRequestAuthorizationFilter
 * Purpose: Enforce role-based authorization rules specifically for funding request related endpoints.
 *
 * Rules:
 *  - Create funding request (POST /nexus/api/v1/funding-requests) => role FUNDER only.
 *  - Invest in funding request (POST /nexus/api/v1/funding-requests/{id}/investment) => role INVESTOR only.
 *  - Distribute returns (POST /nexus/api/v1/funding-requests/{id}/distribute-returns) => role FUNDER only.
 *
 * This filter assumes JWT authentication has already happened (e.g., JwtAuthFilter),
 * but it will still parse and validate the token to safely extract roles.
 */
@Component
public class FundingRequestAuthorizationFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    public FundingRequestAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            // Only apply logic to funding request related paths; otherwise continue.
            if (!path.startsWith("/api/v1/funding-requests")) {
                return chain.filter(exchange);
            }

            // Expect Authorization header with Bearer token
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
            }

            // Extract roles
            List<String> roles = jwtUtil.extractRoles(token);

            // ---- Authorization Rules ----
            // 1. Create funding request
            if (method.equals("POST") && path.matches("/api/v1/funding-requests/?")) {
                if (!hasRoleIgnoreCase(roles, "FUNDER")) {
                    return onError(exchange, "Only funders can create funding requests", HttpStatus.FORBIDDEN);
                }
            }
            // 2. Invest in funding request
            else if (method.equals("POST") && path.matches("/api/v1/funding-requests/[^/]+/investment/?")) {
                if (!hasRoleIgnoreCase(roles, "INVESTOR")) {
                    return onError(exchange, "Only investors can invest in a funding request", HttpStatus.FORBIDDEN);
                }
            }
            // 3. Distribute returns
            else if (method.equals("POST") && path.matches("/api/v1/funding-requests/[^/]+/distribute-returns/?")) {
                if (!hasRoleIgnoreCase(roles, "FUNDER")) {
                    return onError(exchange, "Only funders can distribute returns", HttpStatus.FORBIDDEN);
                }
            }

            // If all checks pass, proceed
            return chain.filter(exchange);
        };
    }

    private boolean hasRoleIgnoreCase(List<String> roles, String expected) {
        if (roles == null) return false;
        for (String r : roles) {
            if (r != null && r.equalsIgnoreCase(expected)) return true;
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "text/plain");
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

