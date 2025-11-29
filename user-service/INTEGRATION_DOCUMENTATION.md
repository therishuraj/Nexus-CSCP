# User Service Integration Guide

Complete API reference for developers integrating with the User Service microservice.

## Base URL & Authentication

- **Base URL**: `http://localhost:3000/api/v1`
- **Authentication**: Currently open endpoints (authentication handled by API Gateway)
- **Content-Type**: `application/json`
- **Response Format**: JSON with standardized structure

## API Quick Reference Table

| Purpose | Method | Endpoint | Status Codes |
|---------|--------|----------|--------------|
| [Create new user account](#create-user) | POST | `/user` | 201, 400 |
| [Get all users (list view)](#get-all-users) | GET | `/users` | 200, 500 |
| [Get user by ID (detailed)](#get-user-by-id) | GET | `/users/{id}` | 200, 400, 404 |
| [Get multiple users by ID (batch)](#get-users-batch) | POST | `/users/batch` | 200, 400, 500 |
| [Update user information](#update-user) | PUT | `/users/{id}` | 200, 400, 404 |
| [Delete user account](#delete-user) | DELETE | `/users/{id}` | 200, 400, 404, 500 |
| [Validate user credentials](#validate-user) | POST | `/auth/user/validate` | 200, 400, 401 |
| [Deposit money to wallet](#wallet-deposit) | POST | `/wallet/deposit` | 200, 400, 404, 500 |
| [Withdraw money from wallet](#wallet-withdraw) | POST | `/wallet/withdraw` | 200, 400, 404, 500 |
| [Service health check](#health-check) | GET | `/health` | 200 |

---

## API Endpoints

### Create User

**POST** `/api/v1/user`

Create a new user account with roles and optional wallet balance.

#### Request
```http
POST /api/v1/user
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com", 
  "password": "securepassword123",
  "roles": ["SUPPLIER", "FUNDER"],
  "walletBalance": 1000.00
}
```

#### Request Fields
- `name` (string, required): User's full name (2-50 characters, letters/spaces/hyphens)
- `email` (string, required): Valid email address (unique in system)
- `password` (string, required): Password (6-100 characters) 
- `roles` (array, required): One or more of: SUPPLIER, FUNDER, INVESTOR, ADMIN
- `walletBalance` (number, optional): Initial wallet balance (defaults to 0.00)

#### Success Response (201 Created)
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": "674c8b3d1234567890abcdef",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "roles": ["SUPPLIER", "FUNDER"],
    "verified": false,
    "walletBalance": 1000.00,
    "fundingRequestIds": [],
    "createdAt": "2025-11-19T20:15:30.123",
    "updatedAt": "2025-11-19T20:15:30.123"
  },
  "timestamp": "2025-11-19T20:15:30.456"
}
```

#### Error Responses
```json
// 400 Bad Request - Validation Error
{
  "success": false,
  "error": "Invalid email format",
  "timestamp": "2025-11-19T20:15:30.456"
}

// 400 Bad Request - Duplicate Email  
{
  "success": false,
  "error": "User already exists with email: john.doe@example.com",
  "timestamp": "2025-11-19T20:15:30.456"
}
```


---

### Get All Users

**GET** `/api/v1/users`

Retrieve all users with limited fields (list view for administrative purposes).

#### Request
```http
GET /api/v1/users
```

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Users retrieved successfully", 
  "data": [
    {
      "id": "674c8b3d1234567890abcdef",
      "name": "John Doe",
      "roles": ["SUPPLIER", "FUNDER"],
      "walletBalance": 1000.00
    },
    {
      "id": "674c8b3d1234567890abcd12",
      "name": "Alice Johnson", 
      "roles": ["INVESTOR"],
      "walletBalance": 5000.00
    }
  ],
  "timestamp": "2025-11-19T20:20:15.789"
}
```

#### Error Response
```json
// 500 Internal Server Error
{
  "success": false,
  "error": "Database connection error",
  "timestamp": "2025-11-19T20:20:15.789"
}
```


---

### Get User by ID

**GET** `/api/v1/users/{id}`

Retrieve detailed information for a specific user.

#### Request
```http
GET /api/v1/users/674c8b3d1234567890abcdef
```

#### URL Parameters
- `id` (string, required): MongoDB ObjectId (24 hex characters)

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": "674c8b3d1234567890abcdef", 
    "name": "John Doe",
    "email": "john.doe@example.com",
    "roles": ["SUPPLIER", "FUNDER"],
    "verified": false,
    "walletBalance": 1000.00,
    "fundingRequestIds": ["fund-001", "fund-002"],
    "createdAt": "2025-11-19T20:15:30.123",
    "updatedAt": "2025-11-19T20:25:45.678"
  },
  "timestamp": "2025-11-19T20:30:12.345"
}
```

#### Error Responses
```json
// 400 Bad Request - Invalid ID Format
{
  "success": false,
  "error": "Invalid user ID format", 
  "timestamp": "2025-11-19T20:30:12.345"
}

// 404 Not Found
{
  "success": false,
  "error": "User not found",
  "timestamp": "2025-11-19T20:30:12.345"
}
```


---

### Get Users Batch

**POST** `/api/v1/users/batch`

Retrieve multiple users by their IDs in a single optimized database query. Returns user data for found users and null values for not found users, maintaining the order of requested IDs.

#### Request
```http
POST /api/v1/users/batch
Content-Type: application/json

{
  "userIds": [
    "674c8b3d1234567890abcdef",
    "674c8b3d1234567890abcd12",
    "674c8b3d1234567890abcd34",
    "507f1f77bcf86cd799439011"
  ]
}
```

#### Request Fields
- `userIds` (array, required): Array of MongoDB ObjectIds (24 hex characters each)
  - Must contain at least one user ID
  - Each ID must be a valid MongoDB ObjectId format
  - No maximum limit on batch size
  - Duplicates are allowed and will return duplicate entries

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Batch user lookup completed",
  "data": [
    {
      "id": "674c8b3d1234567890abcdef",
      "email": "john.doe@example.com",
      "roles": ["ADMIN"]
    },
    {
      "id": "674c8b3d1234567890abcd12",
      "email": "jane.smith@example.com", 
      "roles": ["SUPPLIER"]
    },
    {
      "id": "674c8b3d1234567890abcd34",
      "email": "bob.wilson@example.com",
      "roles": ["FUNDER"]
    },
    {
      "id": "507f1f77bcf86cd799439011",
      "email": null,
      "roles": null
    }
  ],
  "timestamp": "2025-11-21T17:48:18.179"
}
```

#### Response Fields
- `data` (array): Array of user objects in same order as requested
  - `id` (string): The requested user ID
  - `email` (string|null): User's email address (null if user not found)
  - `roles` (array|null): User's roles array (null if user not found)

#### Performance Features
- **Single Database Query**: Uses optimized `findAllById()` for efficient batch retrieval
- **Order Preservation**: Results returned in same order as requested user IDs
- **Memory Efficient**: Processes large batches without excessive memory usage
- **Fast Execution**: Typical response time 37-44ms for 2-4 users

#### Error Responses
```json
// 400 Bad Request - Empty User IDs Array
{
  "success": false,
  "error": "User IDs are required",
  "timestamp": "2025-11-21T17:48:09.006"
}

// 400 Bad Request - Invalid User ID Format
{
  "success": false,
  "error": "Invalid user ID format: invalidid123",
  "timestamp": "2025-11-21T17:47:50.378"
}

// 500 Internal Server Error - Database Error
{
  "success": false,
  "error": "Database connection error",
  "timestamp": "2025-11-21T17:48:18.179"
}
```

#### Usage Examples

##### All Users Found
```bash
curl -X POST http://localhost:3000/api/v1/users/batch \
  -H "Content-Type: application/json" \
  -d '{"userIds": ["674c8b3d1234567890abcdef", "674c8b3d1234567890abcd12"]}'
```

##### Mixed Results (Found + Not Found)  
```bash
curl -X POST http://localhost:3000/api/v1/users/batch \
  -H "Content-Type: application/json" \
  -d '{"userIds": ["674c8b3d1234567890abcdef", "507f1f77bcf86cd799439011"]}'
```

---

### Update User

**PUT** `/api/v1/users/{id}`

Update user information including wallet operations and funding request management.

#### Request Types

##### Basic Information Update
```http
PUT /api/v1/users/674c8b3d1234567890abcdef
Content-Type: application/json

{
  "name": "John Smith",
  "email": "john.smith@example.com"
}
```

##### Direct Wallet Balance Set
```http  
PUT /api/v1/users/674c8b3d1234567890abcdef
Content-Type: application/json

{
  "walletBalance": 2500.00
}
```

##### Wallet Adjustment (Add/Deduct)
```http
PUT /api/v1/users/674c8b3d1234567890abcdef
Content-Type: application/json

{
  "walletAdjustment": 500.00   // Add $500
}

{
  "walletAdjustment": -200.00  // Deduct $200
}
```

##### Funding Request ID Management
```http
PUT /api/v1/users/674c8b3d1234567890abcdef  
Content-Type: application/json

{
  "fundingRequestIds": ["fund-003", "fund-004"]
}
```

#### Request Fields
- `name` (string, optional): Updated user name
- `email` (string, optional): Updated email address (must be unique)
- `walletBalance` (number, optional): Set absolute wallet balance
- `walletAdjustment` (number, optional): Add/subtract from current balance (positive/negative values)
- `fundingRequestIds` (array, optional): Funding request IDs to append (avoids duplicates)

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": "674c8b3d1234567890abcdef",
    "name": "John Smith",
    "email": "john.smith@example.com", 
    "roles": ["SUPPLIER", "FUNDER"],
    "verified": false,
    "walletBalance": 1300.00,
    "fundingRequestIds": ["fund-001", "fund-002", "fund-003", "fund-004"],
    "createdAt": "2025-11-19T20:15:30.123",
    "updatedAt": "2025-11-19T20:35:20.456"
  },
  "timestamp": "2025-11-19T20:35:20.789"
}
```

#### Error Responses
```json
// 400 Bad Request - Insufficient Funds
{
  "success": false,
  "error": "Insufficient funds. Current balance: 1000, Requested deduction: 2000",
  "timestamp": "2025-11-19T20:35:20.789"  
}

// 400 Bad Request - Validation Error
{
  "success": false,
  "error": "At least one field (name, email, walletBalance, walletAdjustment, or fundingRequestIds) must be provided for update",
  "timestamp": "2025-11-19T20:35:20.789"
}

// 404 Not Found
{
  "success": false,
  "error": "User not found", 
  "timestamp": "2025-11-19T20:35:20.789"
}
```


---

### Delete User

**DELETE** `/api/v1/users/{id}`

Delete a user account from the system.

#### Request
```http
DELETE /api/v1/users/674c8b3d1234567890abcdef
```

#### URL Parameters
- `id` (string, required): MongoDB ObjectId (24 hex characters)

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "User deleted successfully", 
  "timestamp": "2025-11-19T20:40:15.123"
}
```

#### Error Responses
```json
// 400 Bad Request - Invalid ID
{
  "success": false,
  "error": "Invalid user ID format",
  "timestamp": "2025-11-19T20:40:15.123"
}

// 404 Not Found
{
  "success": false,
  "error": "User not found",
  "timestamp": "2025-11-19T20:40:15.123"
}

// 500 Internal Server Error
{
  "success": false,
  "error": "Failed to delete user", 
  "timestamp": "2025-11-19T20:40:15.123"
}
```


---

### Validate User

**POST** `/api/v1/auth/user/validate`

Validate user credentials for service-to-service authentication.

#### Request
```http
POST /api/v1/auth/user/validate
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securepassword123"
}
```

#### Request Fields
- `email` (string, required): User's email address
- `password` (string, required): User's password

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "User validation successful",
  "data": {
    "id": "674c8b3d1234567890abcdef",
    "name": "John Doe", 
    "email": "john.doe@example.com",
    "roles": ["SUPPLIER", "FUNDER"],
    "verified": false,
    "walletBalance": 1000.00,
    "fundingRequestIds": ["fund-001", "fund-002"],
    "createdAt": "2025-11-19T20:15:30.123",
    "updatedAt": "2025-11-19T20:35:20.456"
  },
  "timestamp": "2025-11-19T20:45:12.678"
}
```

#### Error Responses
```json
// 401 Unauthorized - Invalid Credentials
{
  "success": false,
  "error": "Invalid credentials",
  "timestamp": "2025-11-19T20:45:12.678"
}

// 400 Bad Request - Missing Fields
{
  "success": false, 
  "error": "Email is required",
  "timestamp": "2025-11-19T20:45:12.678"
}
```


---

### Health Check

**GET** `/api/v1/health`

Check service health status for monitoring and deployment verification.

#### Request  
```http
GET /api/v1/health
```

#### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "service": "user-service", 
    "timestamp": 1700420567890
  },
  "timestamp": "2025-11-19T20:50:05.123"
}
```

---

## Wallet Operations

### Wallet Deposit

**POST** `/api/v1/wallet/deposit`

Deposit money to user's wallet via payment service integration. This endpoint acts as a proxy to the external payment service, validating the user exists and forwarding the request.

#### Request
```http
POST /api/v1/wallet/deposit
Content-Type: application/json
X-User-Id: 674c8b3d1234567890abcdef

{
  "amount": 1000.00
}
```

#### Request Headers
- `X-User-Id` (string, required): MongoDB ObjectId of the user (24 hex characters)

#### Request Fields  
- `amount` (number, required): Amount to deposit (must be positive)

#### Success Response (200 OK)
```json
{
  "paymentId": "pay_12345678901234567890",
  "status": "SUCCESS",
  "amount": 1000.00,
  "currency": "INR",
  "method": "UPI",
  "timestamp": "2025-11-21T02:15:30.123Z",
  "message": "Payment processed successfully"
}
```

#### Error Responses
```json
// 400 Bad Request - Invalid Amount
{
  "success": false,
  "error": "Amount must be positive",
  "timestamp": "2025-11-21T02:15:30.456"
}

// 400 Bad Request - Invalid User ID Format
{
  "success": false,
  "error": "Invalid user ID format",
  "timestamp": "2025-11-21T02:15:30.456"
}

// 404 Not Found - User Not Found
{
  "success": false,
  "error": "User not found",
  "timestamp": "2025-11-21T02:15:30.456"
}

// 500 Internal Server Error - Payment Service Error
{
  "success": false,
  "error": "Payment service unavailable",
  "timestamp": "2025-11-21T02:15:30.456"
}
```

---

### Wallet Withdraw

**POST** `/api/v1/wallet/withdraw`

Withdraw money from user's wallet via payout service integration. This endpoint validates the user exists, generates a UPI ID from the user's name, and forwards the request to the payout service.

#### Request
```http
POST /api/v1/wallet/withdraw
Content-Type: application/json
X-User-Id: 674c8b3d1234567890abcdef

{
  "amount": 500.00
}
```

#### Request Headers
- `X-User-Id` (string, required): MongoDB ObjectId of the user (24 hex characters)

#### Request Fields
- `amount` (number, required): Amount to withdraw (must be positive)

#### UPI ID Generation
The system automatically generates a UPI ID by sanitizing the user's name:
- Converts to lowercase
- Removes all non-alphanumeric characters
- Appends "@upi" suffix
- Example: "John Doe Jr." → "johndoejr@upi"

#### Success Response (200 OK)
```json
{
  "payoutId": "payout_09876543210987654321",
  "status": "SUCCESS", 
  "amount": 500.00,
  "currency": "INR",
  "upiId": "johndoejr@upi",
  "timestamp": "2025-11-21T02:20:45.678Z",
  "message": "Payout processed successfully"
}
```

#### Error Responses
```json
// 400 Bad Request - Invalid Amount
{
  "success": false,
  "error": "Amount must be positive",
  "timestamp": "2025-11-21T02:20:45.678"
}

// 400 Bad Request - Invalid User ID Format  
{
  "success": false,
  "error": "Invalid user ID format",
  "timestamp": "2025-11-21T02:20:45.678"
}

// 404 Not Found - User Not Found
{
  "success": false,
  "error": "User not found", 
  "timestamp": "2025-11-21T02:20:45.678"
}

// 500 Internal Server Error - Payout Service Error
{
  "success": false,
  "error": "Payout service unavailable",
  "timestamp": "2025-11-21T02:20:45.678"
}
```

---

## Testing with Bruno

Import the Bruno collection from `bruno-user-service/` directory and run tests against your local environment.
