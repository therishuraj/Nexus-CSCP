# Order Service - Logging Implementation

## Overview
Comprehensive logging has been added throughout the order-service to improve observability, debugging, and monitoring capabilities in production environments.

## Logging Levels Used

- **INFO**: Request/response logging, successful operations, key business events
- **DEBUG**: Detailed query operations, internal state changes
- **WARN**: Potential issues, missing data that doesn't cause failure
- **ERROR**: Exceptions, failed operations, service communication failures

## Components Enhanced with Logging

### 1. Controllers (INFO level)

#### OrderCommandController
- **Request logging**: Logs incoming requests with all parameters
- **Response logging**: Logs successful responses with order IDs and execution time
- **Error logging**: Logs exceptions with full context and execution time
- **Metrics**: Tracks execution time for each request

Example logs:
```
INFO  - Received place order request - ProductId: P123, Quantity: 5, FunderId: F456, SupplierId: S789, RequestId: R012
INFO  - Order placed successfully - OrderId: O345, ExecutionTime: 234ms
ERROR - Failed to place order - ProductId: P123, Error: Insufficient stock, ExecutionTime: 187ms
```

#### OrderQueryController
- **Request logging**: Logs query requests (by user, by ID)
- **Response logging**: Logs result count and execution time
- **Error logging**: Logs query failures with context

Example logs:
```
INFO  - Fetching orders for user - UserId: U123
INFO  - Retrieved 12 orders for user - UserId: U123, ExecutionTime: 45ms
ERROR - Failed to retrieve orders for user - UserId: U123, Error: Database timeout, ExecutionTime: 5003ms
```

### 2. Service Layer (Already has extensive logging)

#### OrderCommandService
Existing comprehensive logging includes:
- Order placement flow tracking
- Funding request validation
- Product availability checks
- Wallet operations
- Inventory updates
- Status transitions
- Kafka message sending
- Payment transfers

### 3. Client Layer

#### ProductServiceClient (NEW)
- **Request logging**: Logs outbound requests to product-service
- **Response logging**: Logs successful responses with product details
- **Error logging**: Logs WebClient exceptions with HTTP status codes

Example logs:
```
INFO  - Fetching product from product-service - ProductId: P123, URL: http://product-service:3002/api/v1/product/P123
INFO  - Product fetched successfully - ProductId: P123, Name: Widget, Price: 99.99, Quantity: 100
ERROR - Failed to fetch product - ProductId: P123, Status: 404 NOT_FOUND, Error: Product not found
```

#### UserServiceClient (Already has logging)
- Wallet update operations
- Batch email retrieval
- Error handling for service communication

#### InvestmentServiceClient (Already has logging)
- Funding request validation
- Service communication errors

### 4. Query Service (NEW - DEBUG level)

#### OrderQueryService
- **Query logging**: Logs database queries with parameters
- **Result logging**: Logs query results count
- **Not found warnings**: Logs when entities are not found

Example logs:
```
DEBUG - Querying orders by user - UserId: U123
DEBUG - Found 12 orders for user - UserId: U123
WARN  - Order not found - OrderId: O999
```

### 5. Global Exception Handler (NEW)

#### GlobalExceptionHandler
- **Centralized error logging**: Catches all unhandled exceptions
- **Structured error responses**: Returns consistent error format
- **Exception categorization**: Different handling for:
  - IllegalArgumentException (400 Bad Request)
  - WebClientResponseException (service communication errors)
  - RuntimeException (500 Internal Server Error)
  - Generic Exception (catch-all)

Example logs:
```
ERROR - IllegalArgumentException: Invalid product ID format - URI: uri=/api/v1/orders
ERROR - WebClientResponseException: 404 Not Found - Status: 404 NOT_FOUND - URI: uri=/api/v1/orders/O123/status
ERROR - Unhandled Exception: NullPointerException - URI: uri=/api/v1/orders
```

## Execution Time Tracking

All controller methods track and log execution time in milliseconds:
- Helps identify performance bottlenecks
- Tracks slow requests
- Monitors service degradation

## Logging Patterns

### Standard Request/Response Pattern
```java
log.info("Received [operation] request - [key parameters]");
try {
    // operation
    long executionTime = System.currentTimeMillis() - startTime;
    log.info("Operation successful - [result], ExecutionTime: {}ms", executionTime);
} catch (Exception e) {
    long executionTime = System.currentTimeMillis() - startTime;
    log.error("Operation failed - Error: {}, ExecutionTime: {}ms", e.getMessage(), executionTime, e);
    throw e;
}
```

### Service Communication Pattern
```java
log.info("Calling [service-name] - [parameters], URL: {}", url);
try {
    // WebClient call
    log.info("[Service-name] call successful - [response details]");
} catch (WebClientResponseException e) {
    log.error("Failed to call [service-name] - Status: {}, Error: {}", e.getStatusCode(), e.getMessage());
}
```

## Benefits

1. **Debugging**: Easy to trace request flow through the system
2. **Monitoring**: Performance metrics via execution time tracking
3. **Troubleshooting**: Detailed error context for quick issue resolution
4. **Audit Trail**: Complete history of operations and state changes
5. **Alerting**: Error logs can trigger alerts in production monitoring tools

## Production Considerations

### Recommended Log Levels by Environment
- **Development**: DEBUG or INFO
- **Staging**: INFO
- **Production**: INFO (with ERROR alerts configured)

### Log Aggregation
These logs are designed to work well with log aggregation tools like:
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Splunk
- CloudWatch (AWS)
- Datadog

### Performance Impact
- SLF4J is highly optimized with minimal overhead
- Parameterized logging (using {}) avoids string concatenation when log level is disabled
- Execution time tracking uses System.currentTimeMillis() which has minimal impact

## Configuration

Logging configuration should be set in `application.properties` or `application.yml`:

```properties
# Root logging level
logging.level.root=INFO

# Order service specific logging
logging.level.com.razz.orderservice=INFO

# Debug level for query operations (if needed)
logging.level.com.razz.orderservice.query=DEBUG

# Console output pattern
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# File output (recommended for production)
logging.file.name=logs/order-service.log
logging.file.max-size=10MB
logging.file.max-history=30
```

## Files Modified

1. `/order-service/src/main/java/com/razz/orderservice/command/controller/OrderCommandController.java`
2. `/order-service/src/main/java/com/razz/orderservice/query/controller/OrderQueryController.java`
3. `/order-service/src/main/java/com/razz/orderservice/query/service/OrderQueryService.java`
4. `/order-service/src/main/java/com/razz/orderservice/client/ProductServiceClient.java`

## Files Created

1. `/order-service/src/main/java/com/razz/orderservice/exception/GlobalExceptionHandler.java`

## Summary

The order-service now has comprehensive logging at all layers:
- ✅ Controller layer (request/response/errors/timing)
- ✅ Service layer (business logic flow)
- ✅ Client layer (external service calls)
- ✅ Query layer (database operations)
- ✅ Global exception handling (centralized error logging)

All logs follow consistent patterns with structured information, making them easy to parse and analyze in production environments.
