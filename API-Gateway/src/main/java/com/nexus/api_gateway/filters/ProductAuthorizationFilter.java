package com.nexus.api_gateway.filters;

import com.nexus.api_gateway.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ProductAuthorizationFilter
 *
 * Enforces role-based rules for product-related endpoints:
 *   - POST / PUT requests to /nexus/api/v1/product** or /nexus/api/v1/products** MUST have role SUPPLIER.
 *   - Other roles (FUNDER, INVESTOR, etc.) may perform only safe (read-only) methods like GET/HEAD/OPTIONS.
 *
 * Assumptions:
 *   - JwtAuthFilter already validated the token for protected routes, but we still parse it here
 *     to reliably obtain roles (headers for roles are not currently set by JwtAuthFilter).
 *   - Paths may appear with either singular or plural form: /product or /products.
 */
@Component
public class ProductAuthorizationFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    public ProductAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            // Only act on product related paths; otherwise continue quickly.
            if (!isProductPath(path)) {
                return chain.filter(exchange);
            }

            // Extract bearer token
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
            String token = authHeader.substring(7);

            // Validate token basics
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
            }

            // Extract roles
            List<String> roles = jwtUtil.extractRoles(token);

            // Enforce SUPPLIER for modifying methods (POST/PUT)
            if (isModifyingMethod(method)) {
                if (!hasRoleIgnoreCase(roles, "SUPPLIER")) {
                    return onError(exchange, "Only suppliers are allowed to create or modify product data", HttpStatus.FORBIDDEN);
                }
            }
            // For GET/HEAD/OPTIONS we allow all authenticated roles (already validated token).

            return chain.filter(exchange);
        };
    }

    // ---- Helper methods ----
    private boolean isProductPath(String path) {
        return path.startsWith("/nexus/api/v1/product") || path.startsWith("/nexus/api/v1/products");
    }

    private boolean isModifyingMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method);
    }

    private boolean hasRoleIgnoreCase(List<String> roles, String expected) {
        if (roles == null || expected == null) return false;
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

