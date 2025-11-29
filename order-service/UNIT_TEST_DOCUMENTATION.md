# Order Service - Unit Test Documentation

## Overview
This document describes the comprehensive unit tests created for the order-service module. The tests cover all controllers, services, clients, and exception handlers to ensure code quality and reliability.

## Test Coverage

### 1. Controller Layer Tests

#### OrderCommandControllerTest
**Location**: `src/test/java/com/razz/orderservice/command/controller/OrderCommandControllerTest.java`

**Purpose**: Tests the REST API endpoints for order command operations

**Test Cases**:
- ✅ `place_ShouldReturnOrderResponse_WhenOrderIsPlacedSuccessfully`
  - Verifies successful order placement returns correct response with order ID and status
  
- ✅ `place_ShouldThrowException_WhenServiceThrowsException`
  - Validates exception handling when service layer throws IllegalArgumentException
  
- ✅ `updateStatus_ShouldReturnStatusResponse_WhenStatusIsUpdatedSuccessfully`
  - Checks successful status update returns correct response
  
- ✅ `updateStatus_ShouldThrowException_WhenServiceThrowsException`
  - Tests exception propagation from service layer
  
- ✅ `place_ShouldHandleRuntimeException`
  - Validates handling of unexpected runtime exceptions
  
- ✅ `updateStatus_ShouldHandleRuntimeException`
  - Tests runtime exception handling for status updates

**Mocked Dependencies**:
- `OrderCommandService` - Service layer is mocked to isolate controller logic

**Key Assertions**:
- HTTP status codes (200 OK)
- Response body structure and content
- Service method invocation count
- Exception types and messages

---

#### OrderQueryControllerTest
**Location**: `src/test/java/com/razz/orderservice/query/controller/OrderQueryControllerTest.java`

**Purpose**: Tests the REST API endpoints for order query operations

**Test Cases**:
- ✅ `getByUser_ShouldReturnListOfOrders_WhenOrdersExist`
  - Verifies retrieval of orders for a specific user
  
- ✅ `getByUser_ShouldReturnEmptyList_WhenNoOrdersExist`
  - Tests behavior when no orders exist for user
  
- ✅ `getById_ShouldReturnOrder_WhenOrderExists`
  - Validates successful order retrieval by ID
  
- ✅ `getById_ShouldReturnNull_WhenOrderDoesNotExist`
  - Tests null response for non-existent orders
  
- ✅ `getByUser_ShouldHandleException_WhenServiceThrowsException`
  - Verifies exception handling for user query
  
- ✅ `getById_ShouldHandleException_WhenServiceThrowsException`
  - Tests exception handling for ID-based query

**Mocked Dependencies**:
- `OrderQueryService` - Query service is mocked

**Key Assertions**:
- List sizes and content
- OrderView field values
- Null handling
- Exception propagation

---

### 2. Service Layer Tests

#### OrderQueryServiceTest
**Location**: `src/test/java/com/razz/orderservice/query/service/OrderQueryServiceTest.java`

**Purpose**: Tests the business logic for order query operations

**Test Cases**:
- ✅ `getByUser_ShouldReturnListOfOrders_WhenOrdersExistForFunder`
  - Tests query for orders where user is the funder
  
- ✅ `getByUser_ShouldReturnListOfOrders_WhenOrdersExistForSupplier`
  - Tests query for orders where user is the supplier
  
- ✅ `getByUser_ShouldReturnEmptyList_WhenNoOrdersExist`
  - Validates empty result handling
  
- ✅ `getById_ShouldReturnOrder_WhenOrderExists`
  - Tests single order retrieval with all fields
  
- ✅ `getById_ShouldReturnNull_WhenOrderDoesNotExist`
  - Validates null response for missing orders
  
- ✅ `getByUser_ShouldHandleRepositoryException`
  - Tests exception handling from repository layer
  
- ✅ `getById_ShouldHandleRepositoryException`
  - Validates exception propagation from database
  
- ✅ `getByUser_ShouldReturnCorrectOrderDetails`
  - Comprehensive validation of all order fields

**Mocked Dependencies**:
- `OrderViewRepository` - MongoDB repository is mocked

**Key Assertions**:
- Repository method calls (findByFunderNameOrSupplierName, findByOrderId)
- OrderView field values (orderId, status, funderName, supplierName, productName, quantity, totalAmount)
- Exception types and handling

---

### 3. Client Layer Tests

#### ProductServiceClientTest
**Location**: `src/test/java/com/razz/orderservice/client/ProductServiceClientTest.java`

**Purpose**: Tests the WebClient integration with product-service

**Test Cases**:
- ✅ `getProductById_ShouldReturnProduct_WhenProductExists`
  - Tests successful product retrieval
  
- ✅ `getProductById_ShouldReturnNull_WhenProductNotFound`
  - Validates handling of non-existent products
  
- ✅ `getProductById_ShouldThrowException_WhenWebClientThrowsException`
  - Tests exception handling for HTTP errors (404, 500, etc.)
  
- ✅ `updateProductQuantity_ShouldUpdateSuccessfully`
  - Verifies successful product quantity update
  
- ✅ `updateProductQuantity_ShouldThrowException_WhenWebClientFails`
  - Tests error handling for failed updates
  
- ✅ `updateProductQuantity_ShouldCreateCorrectUpdatedProduct`
  - Validates the updated product DTO is created correctly

**Mocked Dependencies**:
- `WebClient` and related specs (RequestHeadersUriSpec, RequestBodyUriSpec, etc.)

**Key Assertions**:
- ProductResponse field values
- WebClient method call chain
- Exception types (WebClientResponseException)
- Request body content for updates

---

### 4. Exception Handler Tests

#### GlobalExceptionHandlerTest
**Location**: `src/test/java/com/razz/orderservice/exception/GlobalExceptionHandlerTest.java`

**Purpose**: Tests centralized exception handling across the application

**Test Cases**:
- ✅ `handleIllegalArgumentException_ShouldReturnBadRequest`
  - Tests 400 Bad Request response for validation errors
  
- ✅ `handleRuntimeException_ShouldReturnInternalServerError`
  - Validates 500 Internal Server Error for runtime exceptions
  
- ✅ `handleWebClientResponseException_ShouldReturnServiceCommunicationError`
  - Tests handling of external service communication failures
  
- ✅ `handleGlobalException_ShouldReturnInternalServerError`
  - Validates catch-all exception handler
  
- ✅ `handleIllegalArgumentException_ShouldStripUriPrefix`
  - Tests URI path formatting in error response
  
- ✅ `handleWebClientResponseException_ShouldHandleDifferentStatusCodes`
  - Validates handling of various HTTP status codes (404, 503, etc.)
  
- ✅ `handleRuntimeException_ShouldHandleNullMessage`
  - Tests graceful handling of exceptions with null messages

**Mocked Dependencies**:
- `WebRequest` - Mocked to provide request context

**Key Assertions**:
- HTTP status codes (400, 404, 500, 503)
- Error response structure (timestamp, status, error, message, path)
- Error message content
- Timestamp presence

---

## Test Execution

### Running All Tests
```bash
cd /Users/I528997/Desktop/BITS/Project/Nexus/order-service
mvn test
```

### Running Specific Test Class
```bash
mvn test -Dtest=OrderCommandControllerTest
mvn test -Dtest=OrderQueryControllerTest
mvn test -Dtest=OrderQueryServiceTest
mvn test -Dtest=ProductServiceClientTest
mvn test -Dtest=GlobalExceptionHandlerTest
```

### Running Tests with Coverage
```bash
mvn clean test jacoco:report
```

---

## Test Dependencies

All tests use the following frameworks (already included in pom.xml):

1. **JUnit 5** (Jupiter)
   - `@Test`, `@BeforeEach`, `@ExtendWith`
   
2. **Mockito**
   - `@Mock`, `@InjectMocks`
   - `MockitoExtension.class`
   - `when()`, `verify()`, `times()`, `eq()`, `any()`

3. **Spring Boot Test**
   - Included via `spring-boot-starter-test`

4. **AssertJ / JUnit Assertions**
   - `assertEquals()`, `assertNotNull()`, `assertTrue()`, `assertThrows()`, `assertDoesNotThrow()`

---

## Testing Patterns Used

### 1. Arrange-Act-Assert (AAA) Pattern
All tests follow the AAA pattern for clarity:
```java
@Test
void testMethod() {
    // Arrange - Set up test data and mocks
    
    // Act - Execute the method under test
    
    // Assert - Verify the results
}
```

### 2. Mock Isolation
- Controllers test only controller logic (service is mocked)
- Services test only service logic (repository is mocked)
- Clients test only client logic (WebClient is mocked)

### 3. Edge Case Testing
Tests cover:
- ✅ Happy path (successful scenarios)
- ✅ Error cases (exceptions, null values)
- ✅ Boundary conditions (empty lists, null responses)

### 4. Exception Testing
```java
assertThrows(ExceptionType.class, () -> {
    methodThatThrowsException();
});
```

---

## Known Limitations

### 1. Command Service Tests
- `OrderCommandService` tests are not yet implemented due to complexity
- Requires extensive mocking of multiple dependencies:
  - `OrderRepository`
  - `OrderViewRepository`
  - `ProductServiceClient`
  - `UserServiceClient`
  - `InvestmentServiceClient`
  - `KafkaTemplate`

### 2. Integration Tests
- No full integration tests with real database
- WebClient mocking can be complex and may not catch all issues
- Kafka integration not tested

### 3. Warnings
Some tests may show IntelliJ warnings about:
- "setUp is never used" - This is a false positive, @BeforeEach methods are called by JUnit
- "Dereferencing possible null pointer" - Acceptable in tests after assertNotNull()
- "Throwable method result is ignored" - Acceptable pattern for assertThrows()

---

## Future Improvements

### Recommended Additions:
1. **OrderCommandService Tests**
   - Complex but critical to test order placement logic
   - Would require careful mocking of all dependencies

2. **Integration Tests**
   - Use Testcontainers for MongoDB
   - Use EmbeddedKafka for Kafka testing
   - Test full request-response flows

3. **Performance Tests**
   - Test response times under load
   - Validate timeout handling

4. **Contract Tests**
   - Verify API contracts with external services
   - Use Spring Cloud Contract or Pact

5. **Mutation Testing**
   - Use PITest to verify test quality
   - Ensure tests actually catch bugs

---

## Test Statistics

| Component | Test Class | Test Methods | Coverage Target |
|-----------|-----------|--------------|-----------------|
| OrderCommandController | OrderCommandControllerTest | 6 | 100% |
| OrderQueryController | OrderQueryControllerTest | 6 | 100% |
| OrderQueryService | OrderQueryServiceTest | 9 | 100% |
| ProductServiceClient | ProductServiceClientTest | 6 | ~80% |
| GlobalExceptionHandler | GlobalExceptionHandlerTest | 7 | 100% |
| **Total** | **5 test classes** | **34 test methods** | **~85%** |

---

## Continuous Integration

### Pre-commit Checks
```bash
# Run tests before committing
mvn clean test

# Check for compilation errors
mvn clean compile
```

### CI/CD Pipeline
Recommended GitHub Actions / Jenkins configuration:
```yaml
- name: Run Tests
  run: mvn clean test
  
- name: Generate Coverage Report
  run: mvn jacoco:report
  
- name: Upload Coverage
  uses: codecov/codecov-action@v3
```

---

## Troubleshooting

### Common Issues:

1. **Tests fail with NullPointerException**
   - Ensure all @Mock fields are properly initialized
   - Verify @ExtendWith(MockitoExtension.class) is present

2. **Mocks not working**
   - Check method signatures match exactly
   - Use `any()` matchers for complex objects
   - Verify `when()` statements are called before the actual method

3. **Compilation errors**
   - Ensure DTOs match actual class structure
   - Check import statements
   - Verify Maven dependencies are up to date

---

## Summary

The order-service now has comprehensive unit test coverage for:
- ✅ All controller endpoints (command and query)
- ✅ Query service business logic
- ✅ Product service client integration
- ✅ Global exception handling
- ⚠️ Command service (pending - complex)
- ⚠️ User/Investment clients (similar to ProductServiceClient)

All tests follow best practices with proper mocking, assertions, and coverage of both success and error scenarios.
