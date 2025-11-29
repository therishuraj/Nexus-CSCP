# Payment Service

A Spring Boot microservice for financial transaction processing, providing deposit and withdrawal operations with user wallet integration in a microservice architecture.

## Overview

The Payment Service is a core financial component of the Nexus microservice ecosystem, handling all monetary transactions including deposits (incoming payments) and withdrawals (outgoing payouts). It integrates seamlessly with the User Service for wallet management and provides a clean API for other microservices to process financial operations.

## Features

- **Deposit Processing** - Handle incoming payments and add funds to user wallets
- **Withdrawal Processing** - Process outgoing payouts and deduct funds from user wallets
- **User Service Integration** - Automatic wallet balance updates via WebClient
- **Admin Wallet Management** - Centralized admin account for fund distribution
- **Demo Mode Simulation** - Complete transaction flow without real payment gateway integration
- **MongoDB Integration** - Document-based storage with Spring Data MongoDB
- **Centralized Exception Handling** - Unified error management with proper HTTP status codes
- **Comprehensive Validation** - Input validation and business rule enforcement

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Data MongoDB** - Database integration
- **Spring WebFlux** - Reactive web client for service communication
- **Maven** - Dependency management and build tool
- **MongoDB** - Document database
- **SLF4J + Logback** - Logging framework

## Quick Start

1. **Clone and navigate to payment-service**
2. **Configure MongoDB connection** in `application.properties`
3. **Set admin user ID** for wallet management
4. **Run the service**: `mvn spring-boot:run`
5. **Service runs on**: `http://localhost:3006`
6. **API Documentation**: See [Integration Documentation](./INTEGRATION_DOCUMENTATION.md)

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **MongoDB** (local installation or MongoDB Atlas)
- **User Service** running for wallet operations

## Installation & Setup

### Database Configuration

Update `application.properties`:
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/payment_service

# Payment Service Configuration
payment.admin.user-id=your_admin_user_id_here

# User Service Integration
user.service.base-url=http://localhost:3000
```

### Environment Setup

```bash
# Navigate to payment-service directory
cd payment-service

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

The service will start on `http://localhost:3006` and automatically create the database collections.

## Project Structure

```
payment-service/
├── pom.xml                           # Maven configuration
├── README.md                         # This file
├── INTEGRATION_DOCUMENTATION.md     # Complete API reference
├── src/main/java/com/payment/paymentservice/
│   ├── PaymentserviceApplication.java # Main application
│   ├── controller/
│   │   ├── PaymentController.java    # Deposit endpoints
│   │   └── PayoutController.java     # Withdrawal endpoints
│   ├── service/
│   │   ├── PaymentService.java       # Deposit business logic
│   │   ├── PayoutService.java        # Withdrawal business logic
│   │   └── UserServiceClient.java    # User service integration
│   ├── repository/
│   │   ├── PaymentRepository.java    # Payment data access
│   │   └── PayoutRepository.java     # Payout data access
│   ├── model/
│   │   ├── Payment.java              # Payment entity
│   │   ├── PaymentStatus.java        # Payment status enum
│   │   ├── Payout.java               # Payout entity
│   │   └── PayoutStatus.java         # Payout status enum
│   ├── dto/
│   │   ├── CreatePaymentRequest.java # Deposit request DTO
│   │   ├── CreatePaymentResponse.java # Deposit response DTO
│   │   ├── CreatePayoutRequest.java  # Withdrawal request DTO
│   │   ├── CreatePayoutResponse.java # Withdrawal response DTO
│   │   └── WalletAdjustmentRequest.java # User service DTO
│   └── config/
│       └── WebClientConfig.java      # Service communication config
└── src/main/resources/
    ├── application.properties        # Configuration
    └── logback-spring.xml           # Logging configuration
```

## Financial Operations

### Deposits (Incoming Payments)
- **Purpose**: Process incoming money and add to user wallets
- **Flow**: 
  1. Create payment record with INITIATED status
  2. Simulate payment processing (demo mode)
  3. Update payment status to SUCCESS
  4. Add funds to user wallet via User Service
  5. Add same amount to admin wallet
  6. Return success response

### Withdrawals (Outgoing Payouts)
- **Purpose**: Process outgoing money and deduct from user wallets
- **Flow**:
  1. Create payout record with INITIATED status
  2. Simulate payout processing (demo mode)
  3. Update payout status to SUCCESS
  4. Deduct funds from admin wallet via User Service
  5. Deduct funds from user wallet via User Service
  6. Return success response

## Configuration

### Application Properties
```properties
# Server Configuration
server.port=3006

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/payment_service

# Payment Service Configuration
payment.admin.user-id=674c8b3d1234567890abcdef

# User Service Integration
user.service.base-url=http://localhost:3000

# Logging Configuration
logging.level.com.payment.paymentservice=DEBUG
```

### Environment Variables
- `MONGODB_URI` - Override MongoDB connection string
- `SERVER_PORT` - Override default port (8080)
- `ADMIN_USER_ID` - Admin user ID for wallet operations
- `USER_SERVICE_URL` - User service base URL

## API Documentation

For complete API integration details, request/response examples, and developer guidance, see:
**[📖 Integration Documentation](./INTEGRATION_DOCUMENTATION.md)**

## Microservice Architecture

This service integrates with:
- **User Service** - Wallet balance management and user operations
- **API Gateway** - Request routing and authentication
- **Investment Service** - Investment-related payments
- **Order Service** - Order-related payments
- **Other Services** - Any service requiring payment processing

### Service Communication
- **Outbound**: WebClient calls to User Service for wallet updates
- **Inbound**: REST API endpoints for deposit/withdrawal requests
- **Data Format**: JSON for all API communications
- **Error Handling**: Standardized error responses with proper HTTP status codes

## Demo Mode Operation

The service currently operates in demo mode:
- **No Real Payment Gateway**: Simulates successful payment processing
- **Instant Processing**: All transactions complete immediately
- **Full Transaction Flow**: Complete business logic without external dependencies
- **Wallet Integration**: Real wallet updates via User Service
- **Audit Trail**: Complete transaction records in MongoDB

## Recent Enhancements

- ✅ **URL Consistency** - Updated to `/deposits` and `/withdrawals` for API standardization
- ✅ **Manual Lombok Replacement** - Removed Lombok dependencies for better compatibility
- ✅ **Enhanced Error Handling** - Comprehensive validation and error responses
- ✅ **User Service Integration** - WebClient-based wallet operations
- ✅ **Demo Mode Implementation** - Complete transaction simulation

## API Quick Reference

| Purpose | Method | Endpoint | Description |
|---------|--------|----------|-------------|
| [Process Deposit](#deposits) | POST | `/api/v1/deposits` | Add funds to user wallet |
| [Process Withdrawal](#withdrawals) | POST | `/api/v1/withdrawals` | Deduct funds from user wallet |

## Migration Notes

**Breaking Changes from Previous Version:**
- **URL Updates**: `/api/v1/payments` → `/api/v1/deposits`
- **URL Updates**: `/api/v1/payouts` → `/api/v1/withdrawals`
- **Impact**: Services calling payment-service endpoints need URL updates
- **Benefit**: Consistent terminology across all microservices

## Contributing

1. Follow the existing code structure and patterns
2. Update integration tests for new features
3. Update documentation for API changes
4. Follow Spring Boot best practices
5. Maintain consistency with User Service patterns

## Health Check

Service health can be monitored through application logs and successful API responses for deployment verification.
