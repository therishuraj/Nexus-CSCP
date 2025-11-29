package com.nexus.api_gateway.controller;

import com.nexus.api_gateway.dto.LoginRequest;
import com.nexus.api_gateway.dto.LoginResponse;
import com.nexus.api_gateway.security.JwtUtil;
import com.nexus.api_gateway.service.UserServiceClient;
import com.nexus.api_gateway.util.UserValidationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * AuthController handles authentication-related endpoints.
 * Uses UserServiceClient for user validation.
 */
@RestController
@RequestMapping("/nexus/auth")
@CrossOrigin("*")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserServiceClient userServiceClient;

    /**
     * Constructor for dependency injection.
     */
    public AuthController(JwtUtil jwtUtil, UserServiceClient userServiceClient) {
        this.jwtUtil = jwtUtil;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Handles user login requests.
     * Validates credentials via UserServiceClient and returns JWT if successful.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest request) {
        // Validate the login request using util class
        if (!UserValidationUtil.isValidLoginRequest(request)) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid login request"));
        }

        Mono<Map<String, Object>> userValidationMono = userServiceClient.validateUser(request);

        Mono<ResponseEntity<?>> responseMono = userValidationMono.flatMap(responseMap -> {
            Object dataObj = responseMap.get("data");

            if (!(dataObj instanceof Map)) {
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
            }

            Map<String, Object> userMap = (Map<String, Object>) dataObj;

            // Validate user map using util class
            if (!UserValidationUtil.isValidUserMap(userMap)) {
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
            }

            String email = UserValidationUtil.extractEmail(userMap);
            List<String> roles = UserValidationUtil.extractRoles(userMap);
            String id = UserValidationUtil.extractId(userMap);

            String token = jwtUtil.generateToken(email, roles,id);
            LoginResponse loginResponse = new LoginResponse(token, "Login successful");
            return Mono.just(ResponseEntity.ok(loginResponse));
        });

        return responseMono.onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: " + error.getMessage())));
    }
}
