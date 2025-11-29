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
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

/**
 * OrderAuthorizationFilter enforces role-based rules for order endpoints.
 * Route expected (after StripPrefix=1): /api/v1/orders/**
 * Rules:
 *  - Create order (POST /api/v1/orders) => role FUNDER only.
 *  - Update status (PUT /api/v1/orders/{orderId}/status) => roles SUPPLIER or FUNDER.
 *  - GET access: INVESTOR role is forbidden from any GET on /api/v1/orders/**.
 */
@Component
public class OrderAuthorizationFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(OrderAuthorizationFilter.class);
    private final JwtUtil jwtUtil;

    public OrderAuthorizationFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            if (!isOrderPath(path)) { return chain.filter(exchange); }
            log.debug("OrderAuthorizationFilter path={} method={}", path, method);

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing/invalid Authorization header for orders path={}", path);
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                log.warn("JWT validation failed for orders path={}", path);
                return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
            }
            List<String> roles = jwtUtil.extractRoles(token);
            log.trace("Roles {} for orders path={}", roles, path);

            // Rule: INVESTOR cannot do GET
            if (method.equals("GET") && hasRoleIgnoreCase(roles, "INVESTOR")) {
                log.warn("Investor role attempted GET on orders path={}", path);
                return onError(exchange, "Investors are not allowed to access order details", HttpStatus.FORBIDDEN);
            }
            // Rule: Create order requires FUNDER (POST /api/v1/orders or /api/v1/orders/)
            if (method.equals("POST") && path.matches("/api/v1/orders/?")) {
                if (!hasRoleIgnoreCase(roles, "FUNDER")) {
                    log.warn("Non-funder attempted to create order roles={} path={}", roles, path);
                    return onError(exchange, "Only funders can create orders", HttpStatus.FORBIDDEN);
                }
            }
            // Rule: Update status requires SUPPLIER or FUNDER
            if (method.equals("PUT") && path.matches("/api/v1/orders/[^/]+/status/?")) {
                if (!(hasRoleIgnoreCase(roles, "SUPPLIER") || hasRoleIgnoreCase(roles, "FUNDER"))) {
                    log.warn("Unauthorized status update attempt roles={} path={}", roles, path);
                    return onError(exchange, "Only suppliers or funders can update order status", HttpStatus.FORBIDDEN);
                }
            }
            log.debug("Order authorization passed path={} method={}", path, method);
            return chain.filter(exchange);
        }; }

    private boolean isOrderPath(String path) { return path.startsWith("/api/v1/orders") || path.startsWith("/nexus/api/v1/orders"); }
    private boolean hasRoleIgnoreCase(List<String> roles, String expected) { if (roles == null) return false; for (String r : roles) { if (r != null && r.equalsIgnoreCase(expected)) return true; } return false; }
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) { exchange.getResponse().setStatusCode(status); exchange.getResponse().getHeaders().add("Content-Type", "text/plain"); byte[] bytes = message.getBytes(StandardCharsets.UTF_8); DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes); return exchange.getResponse().writeWith(Mono.just(buffer)); }
}

