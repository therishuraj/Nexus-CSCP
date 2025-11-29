package com.nexus.user_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for User Service API documentation.
 * 
 * This configuration class defines the API documentation metadata including
 * title, description, version, and contact information for the User Service.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User Service API",
        version = "1.0.0",
        description = """
            User management microservice for Nexus collaborative supply chain platform.
            
            This service provides comprehensive user management capabilities including:
            - User account creation and management
            - User authentication and validation
            - Batch user operations for performance optimization
            - User data retrieval and updates
            
            **Technical Stack:** Spring Boot 3.5.7, MongoDB, Java 21
            **Business Owner:** Product Engineering Department
            **Technical Contact:** Backend Engineering Team
            """,
        contact = @Contact(
            name = "Backend Engineering Team",
            email = "backend-support@nexus.com"
        )
    )
)
public class OpenApiConfig {
    // This configuration class enables OpenAPI documentation generation
    // No additional bean configuration required for basic setup
}
