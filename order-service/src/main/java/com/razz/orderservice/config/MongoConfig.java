package com.razz.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.razz.orderservice.repository")
public class MongoConfig {
    // Using Spring Boot's auto-configured MongoTemplate based on spring.data.mongodb.* properties.
    // No explicit beans required for imperative MongoRepository usage.
    // Ensure pom.xml has spring-boot-starter-data-mongodb (NOT reactive) and remove webflux starter.
}
