# Payment Service Integration Guide

Complete API reference for developers integrating with the Payment Service microservice.

## Base URL & Authentication

- **Base URL**: `http://localhost:8080/api/v1`
- **Authentication**: Currently open endpoints (authentication handled by API Gateway)
- **Content-Type**: `application/json`
- **Response Format**: JSON with standardized structure

## API Quick Reference Table

| Purpose | Method | Endpoint | Status Codes |
|---------|--------|----------|--------------|
| [Process deposit (incoming payment)](#process-deposit) | POST | `/deposit` | 200, 400 |
| [Process withdrawal (outgoing payout)](#process-withdrawal) | POST | `/withdraw` | 200, 400 |

---

## API Endpoints

### Process Deposit

**POST** `/api/v1/deposits`

Process incoming payments and add funds to user wallets. Integrates with User Service for wallet management.

#### Request
```http
POST /api/v1/deposits
Content-Type: application/json

{
  "externalUserId": "674c8b3d1234567890abcdef",
  "amount": 1500.00
}
```

#### Request Fields
- `externalUserId` (string, required): User ID from User Service (MongoDB ObjectId format)
- `amount` (number, required): Deposit amount (minimum value: 1)

#### Success Response (200 OK)
```json
{
  "paymentId": "674c8b3d9876543210fedcba",
  "externalUserId": "674c8b3d1234567890abcdef",
  "amount": 1500.0,
  "status": "SUCCESS",
  "message": "Payment processed successfully (demo mode with Razorpay test)."
}
```

#### Response Fields
- `paymentId` (string): MongoDB ObjectId of the created payment record
- `externalUserId` (string): User ID that received the deposit
- `amount` (number): Amount that was deposited
- `status` (string): Transaction status (SUCCESS in demo mode)
- `message` (string): Human-readable transaction result

#### Business Flow
1. **Payment Record Creation**: Creates Payment document with INITIATED status
2. **Demo Processing**: Simulates successful payment processing
3. **Status Update**: Updates payment status to SUCCESS
4. **User Wallet Update**: Adds deposit amount to user's wallet via User Service
5. **Admin Wallet Update**: Adds same amount to admin wallet for fund tracking
6. **Response**: Returns success confirmation with payment details

#### Error Responses
```json
// 400 Bad Request - Validation Error
{
  "timestamp": "2025-11-21T02:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/deposits"
}

// 400 Bad Request - Missing Required Field
{
  "timestamp": "2025-11-21T02:30:00.123Z", 
  "status": 400,
  "error": "Bad Request",
  "message": "externalUserId is required",
  "path": "/api/v1/deposits"
}

// 400 Bad Request - Invalid Amount
{
  "timestamp": "2025-11-21T02:30:00.123Z",
  "status": 400,
  "error": "Bad Request", 
  "message": "Amount must be at least 1",
  "path": "/api/v1/deposits"
}
```

#### Integration Example

```java
// Example service integration
@Service
public class InvestmentService {
    
    @Autowired
    private WebClient paymentServiceClient;
    
    public void processInvestmentDeposit(String userId, double amount) {
        CreatePaymentRequest request = new CreatePaymentRequest(userId, amount);
        
        CreatePaymentResponse response = paymentServiceClient
            .post()
            .uri("/api/v1/deposits")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(CreatePaymentResponse.class)
            .block();
            
        // Handle successful deposit
        log.info("Deposit processed: {}", response.getPaymentId());
    }
}
```

---

### Process Withdrawal

**POST** `/api/v1/withdrawals`

Process outgoing payouts and deduct funds from user wallets. Handles fund withdrawal with admin wallet management.

#### Request
```http
POST /api/v1/withdrawals
Content-Type: application/json

{
  "externalUserId": "674c8b3d1234567890abcdef", 
  "amount": 500.00,
  "upiId": "user@paytm"
}
```

#### Request Fields
- `externalUserId` (string, required): User ID from User Service (MongoDB ObjectId format)
- `amount` (number, required): Withdrawal amount (minimum value: 1)
- `upiId` (string, required): UPI ID for payout destination

#### Success Response (200 OK)
```json
{
  "payoutId": "674c8b3d5432109876abcdef",
  "externalUserId": "674c8b3d1234567890abcdef",
  "amount": 500.0,
  "upiId": "user@paytm",
  "status": "SUCCESS", 
  "message": "Payout processed successfully."
}
```

#### Response Fields
- `payoutId` (string): MongoDB ObjectId of the created payout record
- `externalUserId` (string): User ID that requested the withdrawal
- `amount` (number): Amount that was withdrawn
- `upiId` (string): UPI ID where funds were sent
- `status` (string): Transaction status (SUCCESS in demo mode)
- `message` (string): Human-readable transaction result

#### Business Flow
1. **Payout Record Creation**: Creates Payout document with INITIATED status
2. **Demo Processing**: Simulates successful payout processing
3. **Status Update**: Updates payout status to SUCCESS
4. **Admin Wallet Deduction**: Deducts withdrawal amount from admin wallet
5. **User Wallet Deduction**: Deducts withdrawal amount from user wallet via User Service
6. **Response**: Returns success confirmation with payout details

#### Error Responses
```json
// 400 Bad Request - Validation Error
{
  "timestamp": "2025-11-21T02:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/withdrawals"
}

// 400 Bad Request - Missing UPI ID
{
  "timestamp": "2025-11-21T02:30:00.123Z",
  "status": 400,
  "error": "Bad Request", 
  "message": "upiId is required",
  "path": "/api/v1/withdrawals"
}

// 400 Bad Request - Invalid Amount
{
  "timestamp": "2025-11-21T02:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be at least 1", 
  "path": "/api/v1/withdrawals"
}
```

#### Integration Example

```java
// Example service integration
@Service
public class PayoutService {
    
    @Autowired
    private WebClient paymentServiceClient;
    
    public void processUserWithdrawal(String userId, double amount, String upiId) {
        CreatePayoutRequest request = new CreatePayoutRequest(userId, amount, upiId);
        
        CreatePayoutResponse response = paymentServiceClient
            .post()
            .uri("/api/v1/withdrawals")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(CreatePayoutResponse.class)
            .block();
            
        // Handle successful withdrawal
        log.info("Withdrawal processed: {}", response.getPayoutId());
    }
}
```

---

## Integration Patterns

### Microservice Communication

The Payment Service is designed for service-to-service communication:

```java
// WebClient Configuration Example
@Configuration
public class PaymentServiceConfig {
    
    @Bean
    public WebClient paymentServiceWebClient() {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
```

### Error Handling

Implement comprehensive error handling for payment operations:

```java
@Service
public class PaymentIntegrationService {
    
    public CreatePaymentResponse processDeposit(String userId, double amount) {
        try {
            return paymentServiceClient
                .post()
                .uri("/api/v1/deposits")
                .bodyValue(new CreatePaymentRequest(userId, amount))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new PaymentValidationException(errorBody)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return Mono.error(new PaymentServiceException("Payment service unavailable"));
                })
                .bodyToMono(CreatePaymentResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Payment processing failed for user {}", userId, e);
            throw new PaymentProcessingException("Failed to process payment", e);
        }
    }
}
```

### Wallet Integration Flow

Understanding the wallet management integration:

**For Deposits:**
1. Payment Service receives deposit request
2. Creates payment record in Payment Service database
3. Calls User Service to add funds to user wallet: `PUT /api/v1/users/{userId}` with `{"walletAdjustment": +amount}`
4. Calls User Service to add funds to admin wallet: `PUT /api/v1/users/{adminId}` with `{"walletAdjustment": +amount}`
5. Returns success response

**For Withdrawals:**
1. Payment Service receives withdrawal request
2. Creates payout record in Payment Service database  
3. Calls User Service to deduct funds from admin wallet: `PUT /api/v1/users/{adminId}` with `{"walletAdjustment": -amount}`
4. Calls User Service to deduct funds from user wallet: `PUT /api/v1/users/{userId}` with `{"walletAdjustment": -amount}`
5. Returns success response

---

## Configuration Requirements

### Required Configuration

```properties
# Payment Service Configuration
payment.admin.user-id=674c8b3d1234567890abcdef

# User Service Integration  
user.service.base-url=http://localhost:3000

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/payment_service
```

### User Service Dependency

The Payment Service requires the User Service to be running and accessible:
- **User Service URL**: Configured via `user.service.base-url`
- **Admin User**: Must exist in User Service with sufficient wallet balance for withdrawals
- **API Compatibility**: Uses User Service wallet adjustment endpoints

---

## Demo Mode Behavior

The Payment Service currently operates in demo mode:

### Simulated Processing
- **No External Gateway**: No real payment gateway integration (Razorpay, Stripe, etc.)
- **Instant Success**: All transactions immediately succeed
- **Status Simulation**: Realistic status flow (INITIATED → SUCCESS)
- **Complete Flow**: Full business logic without external dependencies

### Real Integration Points
- **Database Storage**: All transactions stored in MongoDB
- **User Service Calls**: Real wallet updates via User Service API
- **Audit Trail**: Complete transaction history and logging
- **Error Handling**: Proper validation and error responses

### Future Payment Gateway Integration
The service is designed to easily integrate with real payment gateways:
- Replace demo simulation with actual gateway calls
- Add webhook handling for asynchronous payment updates
- Implement retry logic for failed external calls
- Add payment method support (cards, UPI, net banking)

---

## Migration from Previous Version

### URL Changes (Breaking Changes)

**Old Endpoints:**
- `POST /api/v1/payments` → **New:** `POST /api/v1/deposits`
- `POST /api/v1/payouts` → **New:** `POST /api/v1/withdrawals`

### Migration Steps
1. **Update Service Calls**: Change endpoint URLs in all calling services
2. **Update Documentation**: Update any internal API documentation
3. **Test Integration**: Verify all service integrations work with new URLs
4. **Deploy Coordination**: Coordinate deployment of payment-service and dependent services

### Benefits of Migration
- **Consistency**: Aligned with User Service terminology (`/deposits`, `/withdrawals`)
- **Clarity**: More intuitive endpoint names for financial operations
- **Standardization**: Unified API patterns across microservices

---

## Testing and Validation

### Request Validation
- All requests are validated using Jakarta validation annotations
- Missing required fields return 400 Bad Request
- Invalid field values return 400 Bad Request with specific error messages

### Integration Testing
Test payment service integration with sample requests:

```bash
# Test deposit
curl -X POST http://localhost:8080/api/v1/deposits \
  -H "Content-Type: application/json" \
  -d '{"externalUserId":"674c8b3d1234567890abcdef","amount":1000.00}'

# Test withdrawal
curl -X POST http://localhost:8080/api/v1/withdrawals \
  -H "Content-Type: application/json" \
  -d '{"externalUserId":"674c8b3d1234567890abcdef","amount":500.00,"upiId":"user@paytm"}'
```

### Health Monitoring
Monitor service health through:
- Application logs for transaction processing
- MongoDB for transaction records
- User Service integration success rates
- Response time metrics
