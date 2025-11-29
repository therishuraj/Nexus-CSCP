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

/**
 * UserAuthorizationFilter
 *
 * Enforces that a user may only modify their own user resource.
 * Applies to modifying HTTP methods (PUT, PATCH, DELETE) on paths matching:
 *   /nexus/api/v1/users/{id}
 * After StripPrefix=1 this might become /api/v1/users/{id}. We therefore check both forms.
 *
 * Relies on JwtAuthFilter having already added the header: X-User-Id
 */
@Component
public class UserAuthorizationFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    public UserAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            // Only enforce on modifying methods
            if (!isModifyingMethod(method)) {
                return chain.filter(exchange);
            }

            // Only apply to user resource paths
            if (!isUserIdPath(path)) {
                return chain.filter(exchange);
            }
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Invalid Authorization format", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            String authenticatedUserId = jwtUtil.extractUserId(token);
            if (authenticatedUserId == null || authenticatedUserId.isEmpty()) {
                return onError(exchange, "Missing authenticated user id header", HttpStatus.UNAUTHORIZED);
            }

            String targetUserId = extractUserIdFromPath(path);
            if (targetUserId == null) {
                return onError(exchange, "Unable to parse target user id from path", HttpStatus.BAD_REQUEST);
            }

            if (!authenticatedUserId.equals(targetUserId)) {
                return onError(exchange, "You are not allowed to modify another user's data", HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isModifyingMethod(String method) {
        return "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method);
    }

    private boolean isUserIdPath(String path) {
        // Matches either original or post-StripPrefix form
        return path.matches("/nexus/api/v1/users/[^/]+$") || path.matches("/api/v1/users/[^/]+$");
    }

    private String extractUserIdFromPath(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1 || lastSlash == path.length() - 1) return null;
        return path.substring(lastSlash + 1);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "text/plain");
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

