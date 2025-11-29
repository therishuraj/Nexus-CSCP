# User Service

A Spring Boot microservice for comprehensive user management, wallet operations, and credential validation in a microservice architecture.

## Overview

The User Service is a core component of the Nexus microservice ecosystem, providing centralized user management, wallet operations, and authentication services. It handles user lifecycle management, financial transactions, and service-to-service authentication for the entire platform.

## Features

- **Complete User Management** - CRUD operations for user accounts
- **Role-Based Access Control** - Support for SUPPLIER, FUNDER, INVESTOR, and ADMIN roles
- **Wallet Management** - Balance tracking with positive/negative adjustments and insufficient funds validation
- **Wallet Operations** - Deposit and withdraw via payment service integration with UPI ID generation
- **Funding Request Tracking** - Integration with investment service through funding request IDs
- **Service Authentication** - Credential validation for microservice communication
- **MongoDB Integration** - Document-based storage with Spring Data MongoDB
- **Centralized Exception Handling** - Unified error management with proper HTTP status codes
- **Comprehensive Validation** - Input validation and business rule enforcement

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Data MongoDB** - Database integration
- **Spring WebFlux** - Reactive web client for payment service integration
- **Maven** - Dependency management and build tool
- **MongoDB** - Document database
- **Bruno** - API testing and documentation
- **SLF4J + Logback** - Logging framework

## Quick Start

1. **Clone and navigate to user-service**
2. **Configure MongoDB connection** in `application.properties`
3. **Run the service**: `mvn spring-boot:run`
4. **Service runs on**: `http://localhost:3000`
5. **API Documentation**: See [Integration Documentation](./INTEGRATION_DOCUMENTATION.md)

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MongoDB** (local installation or MongoDB Atlas)

## Installation & Setup

### Database Configuration

1. **Rename configuration file**:
   ```bash
   mv src/main/resources/application-template.properties src/main/resources/application.properties
   ```

2. **For Local MongoDB**:
   ```bash
   # Install MongoDB via Homebrew
   brew tap mongodb/brew
   brew install mongodb-community
   brew services start mongodb/brew/mongodb-community
   ```
   
   Update `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/nexus_users
   ```

3. **For MongoDB Atlas (Cloud)**:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/nexus_users?appName=YourApp
   ```

### Environment Setup

```bash
# Navigate to user-service directory
cd user-service

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

The service will start on `http://localhost:3000` and automatically create the database collections.

## Project Structure

```
user-service/
├── pom.xml                           # Maven configuration
├── README.md                         # This file
├── INTEGRATION_DOCUMENTATION.md     # Complete API reference
├── bruno-user-service/               # API test collection
├── src/main/java/com/nexus/user_service/
│   ├── UserServiceApplication.java   # Main application
│   ├── controller/
│   │   └── UserController.java       # REST endpoints
│   ├── service/
│   │   ├── UserService.java          # Service interface
│   │   └── UserServiceImpl.java      # Business logic
│   ├── repository/
│   │   └── UserRepository.java       # Data access layer
│   ├── model/
│   │   └── User.java                 # User entity model
│   ├── dto/
│   │   ├── request/                  # Request DTOs
│   │   └── response/                 # Response DTOs
│   └── utils/
│       ├── ExceptionUtils.java       # Centralized exceptions
│       ├── MapperUtils.java          # DTO mapping
│       ├── ValidationUtils.java      # Input validation
│       ├── ResponseUtils.java        # Response formatting
│       ├── PasswordUtils.java        # Password hashing
│       └── LoggerUtils.java          # Logging utilities
└── src/main/resources/
    └── application.properties        # Configuration
```

## User Roles

### SUPPLIER
- **Purpose**: Product and inventory management
- **Capabilities**: List products, manage stock, confirm deliveries
- **Integrations**: Product service, order service

### FUNDER  
- **Purpose**: Funding and order management
- **Capabilities**: Create funding requests, place orders, process payments
- **Integrations**: Investment service, order service, payment service

### INVESTOR
- **Purpose**: Investment and profit tracking
- **Capabilities**: Fund requests, monitor investments, track profit shares
- **Integrations**: Investment service, payment service

### ADMIN
- **Purpose**: System administration
- **Capabilities**: User management, system oversight, configuration management
- **Integrations**: All services (administrative access)

## Configuration

### Application Properties
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/nexus_users

# Server Configuration
server.port=3000

# Logging Configuration
logging.level.com.nexus.user_service=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
```

### Environment Variables
- `MONGODB_URI` - Override MongoDB connection string
- `SERVER_PORT` - Override default port (3000)
- `LOG_LEVEL` - Override logging level

## API Documentation

For complete API integration details, request/response examples, and developer guidance, see:
**[📖 Integration Documentation](./INTEGRATION_DOCUMENTATION.md)**

## Testing

The service includes a comprehensive Bruno API test collection located in `bruno-user-service/`. This collection includes:
- All CRUD operations
- Wallet management scenarios
- Funding request ID operations
- Error condition testing
- Authentication validation

To run tests:
1. Install Bruno API testing tool
2. Import the `bruno-user-service` collection
3. Configure the local environment
4. Execute test scenarios

## Microservice Architecture

This service integrates with:
- **API Gateway** - Authentication and routing
- **Investment Service** - Funding request management
- **Order Service** - User order processing
- **Payment Service** - Wallet deposit and withdrawal operations via proxy endpoints
- **Product Service** - User-product interactions

## Recent Enhancements

- ✅ **Wallet Adjustments** - Positive/negative amount operations with validation
- ✅ **Funding Request IDs** - Investment service integration tracking
- ✅ **Centralized Exceptions** - Unified error handling with proper HTTP status codes
- ✅ **Enhanced Validation** - Comprehensive input and business rule validation
- ✅ **Wallet Integration** - Deposit/withdraw endpoints with payment service proxy pattern and UPI ID generation

## Contributing

1. Follow the existing code structure and patterns
2. Update tests for new features
3. Update documentation for API changes
4. Follow Spring Boot best practices
5. Use the established DTO mapping patterns

## Health Check

Service health is available at `GET /api/v1/health` for monitoring and deployment verification.
