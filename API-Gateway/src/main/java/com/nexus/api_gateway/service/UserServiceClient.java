package com.nexus.api_gateway.service;

import com.nexus.api_gateway.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Service for communicating with the remote User Service.
 * Handles user validation and response parsing.
 */
@Service
public class UserServiceClient {

    private final WebClient webClient;
    
    @Value("${user.service.url:http://localhost:3000}")
    private String userServiceUrl;

    /**
     * Constructor for dependency injection of WebClient.
     * @param webClient the WebClient bean used for HTTP requests
     */
    public UserServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Calls the user service to validate user credentials.
     * @param request the login request payload (email and password)
     * @return Mono emitting the response map from user service
     */
    public Mono<Map<String, Object>> validateUser(Object request) {
        // Build the validation URI
        String validationUri = userServiceUrl + "/api/v1/auth/user/validate";
        
        // Build the POST request
        WebClient.RequestBodySpec requestSpec = webClient.post().uri(validationUri);

        // Set the request body
        WebClient.RequestHeadersSpec<?> headersSpec = requestSpec.bodyValue(request);

        // Retrieve the response
        WebClient.ResponseSpec responseSpec = headersSpec.retrieve();

        // Handle error status
        WebClient.ResponseSpec errorHandledSpec = responseSpec.onStatus(
                status -> status.isError(),
                clientResponse -> Mono.error(new RuntimeException("Invalid credentials"))
        );

        // Convert response body to Map<String, Object>
        Mono<Map<String, Object>> responseMono = errorHandledSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

        return responseMono;
    }
}
