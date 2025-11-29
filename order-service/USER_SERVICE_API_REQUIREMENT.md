# User Service API Requirement for Order Notifications

## New Endpoint Required in User Service

### Endpoint: Get User Emails in Batch

**Method:** `POST`  
**Path:** `/api/v1/users/batch`  
**Description:** Retrieves user information (id, email, role) for multiple users in a single request

---

## Request Format

### Request Body (JSON)
```json
{
  "userIds": ["userId1", "userId2", "userId3"]
}
```

### Request DTO (Java)
```java
public record UserBatchRequest(List<String> userIds) {
}
```

---

## Response Format

### Response Body (JSON)
```json
[
  {
    "id": "673f333917c065b20466799c",
    "email": "funder@example.com",
    "role": "FUNDER"
  },
  {
    "id": "673f333917c065b20466799d",
    "email": "supplier@example.com",
    "role": "SUPPLIER"
  }
]
```

### Response DTO (Java)
```java
public record UserEmailResponse(String id, String email, String role) {
}
```

---

## Example Implementation in User Service

### Controller
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/batch")
    public List<UserEmailResponse> getUsersByIds(@RequestBody UserBatchRequest request) {
        return userService.getUserEmailsByIds(request.userIds());
    }
}
```

### Service
```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<UserEmailResponse> getUserEmailsByIds(List<String> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        
        return users.stream()
            .map(user -> new UserEmailResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
            ))
            .collect(Collectors.toList());
    }
}
```

---

## Usage in Order Service

When an order is placed, the order-service will:

1. Call `POST /api/v1/users/batch` with funder and supplier IDs
2. Receive email addresses and roles for both users
3. Send two Kafka messages to `orderNotification` topic:
   - One notification to the **funder** (order placed successfully)
   - One notification to the **supplier** (new order received)

### Kafka Message Format (Consumed by Consumer Service)
```json
{
  "key": "orderId123",
  "email": "user@example.com",
  "subject": "Order Placed Successfully",
  "body": "Your order #orderId123 has been placed successfully. Product: Product Name, Quantity: 10, Total Amount: 1000.00",
  "orderId": "orderId123",
  "timestamp": "2025-11-21T10:30:00"
}
```

### Consumer Service Processing
```java
String email = (String) message.get("email");
String subject = (String) message.get("subject");
String body = (String) message.get("body");

// Send email notification
emailService.sendEmail(email, subject, body);
```

---

## Implementation Status

### ✅ Completed in Order Service:
- Created `UserBatchRequest` DTO
- Created `UserEmailResponse` DTO
- Added `getUserEmailsByIds()` method in `UserServiceClient`
- Updated `sendOrderPlacedEvent()` to fetch user emails
- Created `sendNotificationMessage()` to send Kafka messages in correct format
- Topic: `orderNotification`

### ⏳ Required in User Service:
- Implement `POST /api/v1/users/batch` endpoint
- Create `UserBatchRequest` DTO
- Create `UserEmailResponse` DTO
- Implement service method to fetch users by IDs and return email/role info
