package com.nexus.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT generation and validation.
 * Uses the properties defined in application.yml.
 * Updated to handle a List<String> of roles.
 */
@Component
public class JwtUtil {

    // These values are loaded from the 'jwt' section of application.yml
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // milliseconds

    @Value("${jwt.algorithm}")
    private String algorithm; // Not strictly used by JJWT, but good practice

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token containing the username (subject) and a list of roles.
     * @param username The principal identifier (email or user ID).
     * @param roles List of roles (SUPPLIER, FUNDER, INVESTOR, etc.).
     * @return The signed JWT string.
     */
    public String generateToken(String username, List<String> roles, String id) {
        Map<String, Object> claims = new HashMap<>();
        // Store roles as a list in the token payload
        claims.put("roles", roles);
        claims.put("id", id);

        // Note: 'sub' (subject) is set to the username/email here, which serves as the ID
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    /**
     * Extracts the list of roles from the JWT claims.
     * @param token The JWT string.
     * @return List of roles.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
}
