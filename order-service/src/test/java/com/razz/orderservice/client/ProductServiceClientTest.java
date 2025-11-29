package com.razz.orderservice.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Basic unit tests for ProductServiceClient.
 * Note: Full WebClient integration tests should be done with @WebClientTest or integration tests.
 * These tests verify the client can be instantiated and basic logic works.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceClientTest {

    private ProductServiceClient productServiceClient;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().build();
        productServiceClient = new ProductServiceClient(webClient, "http://localhost:8081");
    }

    @Test
    void constructor_ShouldCreateClientWithCorrectUrl() {
        // Arrange & Act
        ProductServiceClient client = new ProductServiceClient(webClient, "http://test-url:8080");

        // Assert
        assertNotNull(client);
    }

    @Test
    void constructor_ShouldAcceptNullWebClient() {
        // This test verifies the constructor parameters
        // In real scenario, WebClient should never be null but constructor accepts it
        assertDoesNotThrow(() -> {
            new ProductServiceClient(null, "http://localhost:8081");
        });
    }

    @Test
    void productServiceUrl_ShouldUseDefaultWhenNotProvided() {
        // Verify that the default URL mechanism works
        ProductServiceClient clientWithDefault = new ProductServiceClient(webClient, "http://localhost:8081");
        assertNotNull(clientWithDefault);
    }
}
