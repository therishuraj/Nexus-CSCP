# Order Service

A Spring Boot microservice for comprehensive order management, implementing CQRS pattern with event-driven architecture for order processing and notifications in a microservice ecosystem.

## Overview

The Order Service is a critical component of the Nexus microservice ecosystem, providing end-to-end order management from placement to delivery. It implements Command Query Responsibility Segregation (CQRS) pattern, integrates with multiple microservices, and publishes events to Kafka for email notifications.

## Features

- **CQRS Architecture** - Separate read and write models for optimized performance
- **Order Lifecycle Management** - From placement through delivery with status tracking
- **Multi-Service Integration** - Coordinates with investment, product, user, and payment services
- **Event-Driven Notifications** - Kafka-based email notifications to funders and suppliers
- **Wallet Operations** - Automated payment processing and fund transfers
- **Funding Validation** - Ensures orders are backed by fully funded investment requests
- **Inventory Management** - Real-time product quantity updates
- **MongoDB Integration** - Dual collection storage (orders and order_views)
- **Comprehensive Logging** - Request/response tracking with execution time metrics
- **Global Exception Handling** - Centralized error management with proper HTTP status codes

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Data MongoDB** - Database integration for read/write models
- **Spring Kafka** - Event streaming and message publishing
- **Spring WebFlux** - Reactive web client for microservice communication
- **Maven** - Dependency management and build tool
- **MongoDB** - Document database (dual collections)
- **Apache Kafka** - Message broker for event notifications
- **Bruno** - API testing and documentation
- **SLF4J + Logback** - Logging framework

## Quick Start

1. **Clone and navigate to order-service**
2. **Configure MongoDB connection** in `application.properties`
3. **Configure Kafka connection** in `application.properties`
4. **Run the service**: `mvn spring-boot:run`
5. **Service runs on**: `http://localhost:3007`
6. **API Documentation**: See Bruno collection in `bruno/` directory

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MongoDB** (local installation or MongoDB Atlas)
- **Apache Kafka** (local installation or cloud service)
- **Running instances** of:
  - User Service (port 3000)
  - Product Service (port 3002)
  - Investment Service (port 3004)

## Installation & Setup

### Database Configuration

1. **For Local MongoDB**:
   ```bash
   # Install MongoDB via Homebrew
   brew tap mongodb/brew
   brew install mongodb-community
   brew services start mongodb/brew/mongodb-community
   ```
   
   Update `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/order-ms
   ```

2. **For MongoDB Atlas (Cloud)**:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/order-ms?appName=YourApp
   ```

### Kafka Configuration

1. **For Local Kafka**:
   ```bash
   # Install Kafka via Homebrew
   brew install kafka
   
   # Start Zookeeper
   zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
   
   # Start Kafka
   kafka-server-start /usr/local/etc/kafka/server.properties
   ```
   
   Update `application.properties`:
   ```properties
   spring.kafka.bootstrap-servers=localhost:9092
   ```

2. **For Docker Kafka**:
   ```properties
   spring.kafka.bootstrap-servers=kafka:9092
   ```

### Environment Setup

```bash
# Navigate to order-service directory
cd order-service

# Install dependencies
mvn clean install

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

The service will start on `http://localhost:3007` and automatically create the database collections (`orders` and `order_views`).

## Project Structure

```
order-service/
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
├── LOGGING_DOCUMENTATION.md                   # Logging implementation guide
├── UNIT_TEST_DOCUMENTATION.md                 # Test coverage documentation
├── USER_SERVICE_API_REQUIREMENT.md            # User service API specification
├── Dockerfile                                 # Docker containerization
├── bruno/                                     # API test collection
│   ├── 01-place-order.bru
│   ├── 03-update-order-status.bru
│   ├── 04-pay-order.bru
│   ├── 05-get-orders-by-user.bru
│   ├── 06-get-order-views.bru
│   └── README.md
├── src/main/java/com/razz/orderservice/
│   ├── OrderServiceApplication.java           # Main application
│   ├── command/                               # CQRS Write side
│   │   ├── controller/
│   │   │   └── OrderCommandController.java    # Command endpoints
│   │   └── service/
│   │       └── OrderCommandService.java       # Order placement & updates
│   ├── query/                                 # CQRS Read side
│   │   ├── controller/
│   │   │   └── OrderQueryController.java      # Query endpoints
│   │   └── service/
│   │       └── OrderQueryService.java         # Order retrieval
│   ├── client/                                # Microservice clients
│   │   ├── InvestmentServiceClient.java       # Funding validation
│   │   ├── ProductServiceClient.java          # Product & inventory
│   │   └── UserServiceClient.java             # User & wallet operations
│   ├── repository/                            # Data access layer
│   │   ├── OrderRepository.java               # Write model repository
│   │   └── OrderViewRepository.java           # Read model repository
│   ├── model/                                 # Domain models
│   │   ├── write/
│   │   │   └── Order.java                     # Command model
│   │   └── read/
│   │       └── OrderView.java                 # Query model (denormalized)
│   ├── dto/                                   # Data transfer objects
│   │   ├── PlaceOrderRequest.java             # Place order command
│   │   ├── OrderResponse.java                 # Order query response
│   │   ├── ProductResponse.java               # Product service DTO
│   │   ├── UserEmailResponse.java             # User email DTO
│   │   ├── FundingRequestResponse.java        # Investment service DTO
│   │   └── ...                                # Other DTOs
│   ├── config/                                # Configuration
│   │   ├── KafkaProducerConfig.java           # Kafka setup
│   │   ├── MongoConfig.java                   # Write DB config
│   │   └── ReactiveMongoConfig.java           # Read DB config
│   └── exception/
│       └── GlobalExceptionHandler.java        # Centralized error handling
└── src/main/resources/
    └── application.properties                 # Configuration
```

## CQRS Architecture

### Write Model (Commands)
- **Collection**: `orders`
- **Purpose**: Handle order creation and status updates
- **Operations**: Place order, update status, process payments
- **Optimized for**: Write performance and consistency

### Read Model (Queries)
- **Collection**: `order_views`
- **Purpose**: Optimized denormalized views for queries
- **Operations**: Get orders by user, get order details
- **Optimized for**: Fast read performance
- **Denormalization**: Contains product names, user names for quick access

### Synchronization
- **Trigger**: After successful command execution
- **Method**: Automatic sync from `orders` to `order_views`
- **Contains**: Pre-joined data (product names, supplier names, funder names)

## Order Lifecycle

1. **PENDING** - Order placed, awaiting processing
2. **PROCESSING** - Order being prepared by supplier
3. **SHIPPED** - Order dispatched for delivery
4. **DELIVERED** - Order received by customer
   - Triggers automatic payment from admin to supplier
   - Marks supplier as paid

## Order Placement Flow

```
1. Validate Funding Request (Investment Service)
   ↓ Status must be "FUNDED"
2. Fetch Product Details (Product Service)
   ↓ Validate availability
3. Deduct from Funder Wallet (User Service)
   ↓ Payment for order
4. Create Order (Write Model)
   ↓ Save to orders collection
5. Sync to Order View (Read Model)
   ↓ Create denormalized view
6. Update Product Inventory (Product Service)
   ↓ Reduce quantity
7. Send Kafka Notifications
   ↓ Email to funder and supplier
```

## Configuration

### Application Properties
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://nexus:nexus5@cluster0.kei0rsa.mongodb.net/order-ms

# Server Configuration
server.port=3007

# Kafka Configuration
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Microservice URLs
product.service.url=http://product-service:3002
user.service.url=http://user-service:3000
investment.service.url=http://investment-service:3004

# Admin Configuration
admin.user.id=691f333917c065b20466799d

# Logging Configuration
logging.level.com.razz.orderservice=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
logging.level.org.springframework.kafka=DEBUG
```

### Environment Variables
- `MONGODB_URI` - Override MongoDB connection string
- `KAFKA_BOOTSTRAP_SERVERS` - Override Kafka broker address
- `SERVER_PORT` - Override default port (3007)
- `PRODUCT_SERVICE_URL` - Product service endpoint
- `USER_SERVICE_URL` - User service endpoint
- `INVESTMENT_SERVICE_URL` - Investment service endpoint
- `ADMIN_USER_ID` - Admin user for payment operations

## API Endpoints

### Command Endpoints (Write Operations)

#### Place Order
```http
POST /api/v1/orders
Content-Type: application/json

{
  "productId": "string",
  "quantity": integer,
  "funderId": "string",
  "supplierId": "string",
  "requestId": "string"
}
```

**Response**:
```json
{
  "id": "673f66b8be2a8d4b75c33f21",
  "status": "PLACED"
}
```

#### Update Order Status
```http
PUT /api/v1/orders/{id}/status
Content-Type: application/json

{
  "status": "PROCESSING|SHIPPED|DELIVERED"
}
```

**Response**:
```json
{
  "status": "DELIVERED"
}
```

### Query Endpoints (Read Operations)

#### Get Orders by User
```http
GET /api/v1/orders?userId={userId}
```

**Response**:
```json
[
  {
    "orderId": "673f66b8be2a8d4b75c33f21",
    "productName": "Widget A",
    "quantity": 5,
    "totalAmount": 499.95,
    "funderName": "John Doe",
    "supplierName": "ABC Suppliers",
    "status": "DELIVERED",
    "supplierPaid": true
  }
]
```

#### Get Order by ID
```http
GET /api/v1/orders/{id}
```

**Response**:
```json
{
  "orderId": "673f66b8be2a8d4b75c33f21",
  "productName": "Widget A",
  "quantity": 5,
  "totalAmount": 499.95,
  "funderName": "John Doe",
  "supplierName": "ABC Suppliers",
  "status": "DELIVERED",
  "supplierPaid": true
}
```

## Event Publishing

### Kafka Topics

#### orderNotification
- **Purpose**: Send email notifications
- **Trigger**: After successful order placement
- **Consumers**: Consumer service (email sender)
- **Message Format**:
```json
{
  "email": "user@example.com",
  "subject": "Order Placed Successfully",
  "body": "Your order #ORDER_ID has been placed...",
  "orderId": "string",
  "timestamp": "ISO-8601"
}
```

**Notification Recipients**:
- **Funder**: "Order Placed Successfully" with order details
- **Supplier**: "New Order Received" with order details

## Microservice Integration

### Investment Service Integration
- **Purpose**: Validate funding request status
- **Endpoint**: `GET /api/v1/funding-requests/{requestId}`
- **Validation**: Ensures status is "FUNDED" before order placement

### Product Service Integration
- **Purpose**: Product details and inventory management
- **Endpoints**: 
  - `GET /api/v1/product/{id}` - Get product details
  - `PUT /api/v1/product/{id}` - Update inventory

### User Service Integration
- **Purpose**: Wallet operations and user details
- **Endpoints**:
  - `POST /api/v1/users/batch` - Get user emails for notifications
  - `PUT /api/v1/users/{id}/wallet/adjust` - Deduct/add funds

## Testing

### Unit Tests Coverage: ~65%

The service includes comprehensive unit tests:
- **OrderCommandController** - 6 tests (place, updateStatus, error handling)
- **OrderQueryController** - 6 tests (getByUser, getById, exception scenarios)
- **OrderQueryService** - 9 tests (all query methods, error scenarios)
- **ProductServiceClient** - 3 tests (constructor validation)
- **GlobalExceptionHandler** - 7 tests (all exception types)

Total: **31 tests** covering controllers, services, clients, and exception handling.

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=OrderCommandControllerTest

# Run with coverage report
mvn clean test jacoco:report
```

### API Testing with Bruno
```bash
# Navigate to bruno directory
cd bruno

# Import collection into Bruno
# Execute test scenarios:
# - Place Order
# - Update Status
# - Get Orders
# - Get Order Views
```

See `bruno/README.md` for detailed API test documentation.

## Logging

The service implements comprehensive logging at all layers:
- **Controller Layer**: Request/response logging with execution time
- **Service Layer**: Business logic flow and decision points
- **Client Layer**: External service calls and responses
- **Exception Handling**: Detailed error context with stack traces

**Log Levels**:
- `INFO` - Business events (order placed, status updated)
- `DEBUG` - Detailed flow for development
- `WARN` - Potential issues (order not found)
- `ERROR` - Critical failures (payment errors, service unavailable)

See [LOGGING_DOCUMENTATION.md](./LOGGING_DOCUMENTATION.md) for complete details.

## Docker Support

### Build Docker Image
```bash
docker build -t order-service:latest .
```

### Run with Docker Compose
```bash
docker-compose up -d order-service
```

### Environment Variables in Docker
All configuration can be overridden via environment variables in `docker-compose.yml`:
```yaml
environment:
  SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/order-ms
  SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
  PRODUCT_SERVICE_URL: http://product-service:3002
  USER_SERVICE_URL: http://user-service:3000
  INVESTMENT_SERVICE_URL: http://investment-service:3004
```

## Recent Enhancements

- ✅ **CQRS Implementation** - Separate read/write models for scalability
- ✅ **Event-Driven Notifications** - Kafka integration for email notifications
- ✅ **Multi-Service Orchestration** - Coordinates 4 microservices per order
- ✅ **Funding Validation** - Integration with investment service
- ✅ **Automated Payments** - Supplier payment on delivery
- ✅ **Comprehensive Logging** - Production-ready observability
- ✅ **Unit Test Coverage** - 65% coverage with 31 tests
- ✅ **Global Exception Handling** - Centralized error management
- ✅ **Docker Support** - Containerized deployment ready

## Monitoring & Observability

### Key Metrics to Monitor
- Order placement success rate
- Average order processing time
- Kafka message publish success rate
- External service call latency
- Database query performance
- Exception rates by type

### Recommended Monitoring Tools
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization
- **ELK Stack** - Log aggregation and analysis
- **Zipkin/Jaeger** - Distributed tracing

## Troubleshooting

### Common Issues

**Order placement fails with "Funding request not found"**
- Check investment-service is running on port 3004
- Verify requestId exists and status is "FUNDED"
- Review investment-service logs

**"Product not found" error**
- Ensure product-service is running on port 3002
- Verify productId exists in product database
- Check product-service connectivity

**"Insufficient wallet balance" error**
- Check funder's wallet balance in user-service
- Verify wallet has enough funds for order total
- Review user-service wallet operations

**Kafka notifications not sending**
- Verify Kafka broker is running on port 9092
- Check topic "orderNotification" exists
- Verify consumer service is running
- Review Kafka producer logs

**Order views not syncing**
- Check MongoDB connection to order_views collection
- Verify sync logic in OrderCommandService
- Check logs for sync operation errors
- Ensure MongoDB has proper write permissions

**WebClient timeout errors**
- Increase timeout configuration in WebClient
- Check network connectivity to external services
- Verify external services are healthy
- Review service response times

## Performance Considerations

### Database Optimization
- **Indexes**: Created on orderId, funderId, supplierId for fast queries
- **Denormalization**: Order views contain pre-joined data to avoid joins
- **Dual Collections**: Separate read/write collections reduce contention

### Caching Strategy (Future Enhancement)
- Cache product details to reduce external calls
- Cache user emails for notification batching
- Implement Redis for distributed caching

### Scalability
- **Horizontal Scaling**: Service is stateless and can be scaled horizontally
- **CQRS Pattern**: Read and write operations can scale independently
- **Async Processing**: Kafka notifications don't block order placement

## Security Considerations

### Current Implementation
- Input validation on all request DTOs
- Exception handling prevents information leakage
- MongoDB connection string externalized

### Future Enhancements
- Add Spring Security for authentication
- Implement JWT token validation
- Add rate limiting for API endpoints
- Encrypt sensitive data in MongoDB
- Add API key validation for service-to-service calls

## Contributing

1. Follow CQRS pattern - keep commands and queries separate
2. Update both write and read models
3. Add comprehensive logging for new features
4. Write unit tests for new methods (maintain 65%+ coverage)
5. Update Bruno API test collection
6. Document API changes in README
7. Follow existing code structure and patterns
8. Add proper exception handling
9. Update LOGGING_DOCUMENTATION.md if adding new log patterns

## Health Check

Service health and metrics are available for monitoring:
- **Application Health**: Monitor via Spring Boot Actuator (if enabled)
- **Kafka Health**: Check message publish success in logs
- **Database Health**: Monitor MongoDB connection in logs
- **Service Dependencies**: Monitor external service call success rates

## Related Documentation

- **[LOGGING_DOCUMENTATION.md](./LOGGING_DOCUMENTATION.md)** - Detailed logging implementation
- **[UNIT_TEST_DOCUMENTATION.md](./UNIT_TEST_DOCUMENTATION.md)** - Test coverage and patterns
- **[USER_SERVICE_API_REQUIREMENT.md](./USER_SERVICE_API_REQUIREMENT.md)** - User service API spec

## License

This is a proprietary service for the Nexus microservice ecosystem.

---

**Service Version**: 0.0.1-SNAPSHOT  
**Spring Boot Version**: 3.5.7  
**Java Version**: 17  
**Last Updated**: November 2025
