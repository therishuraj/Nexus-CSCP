package com.nexus.api_gateway.filters;

import com.nexus.api_gateway.security.JwtUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.nexus.api_gateway.util.Constants.USER_ID_HEADER;

/**
 * JWT Authentication Filter
 * 1. Validates JWT.
 * 2. Extracts user ID (email) from JWT.
 * 3. Sets the X-User-ID header for downstream filters (like RateLimiterFilter).
 */
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;


    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            
            // 1. Skip authentication check for the /auth and /actuator/health routes
            if (request.getURI().getPath().contains("/nexus/auth") || request.getURI().getPath().contains("/actuator/health") || request.getURI().getPath().contains("/nexus/api/v1/user")) {
                // For non-secured routes, just pass a default header for rate limiting
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(USER_ID_HEADER, "ANONYMOUS")
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }

            // 2. Check for Bearer token
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Invalid Authorization format", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // 3. Validate Token and Extract User ID (email)
            try {
                if (!jwtUtil.validateToken(token)) {
                    return this.onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
                }

                String userEmail = jwtUtil.extractUsername(token); // The email is the ID here


                String userId = jwtUtil.extractUserId(token);

                // 4. Modify the request: Add the X-User-ID header
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(USER_ID_HEADER, userEmail)
                        .header("X-User-Id", userId)
                        .build();

                // Continue to the next filter (RateLimiterFilter)
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                return this.onError(exchange, "Error processing JWT: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "text/plain");
        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse()
                                   .bufferFactory()
                                   .wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
