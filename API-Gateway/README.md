# Nexus API Gateway

Simple, detailed guide to understand, run, and extend this Spring Cloud Gateway (Reactive / WebFlux) application.

---
## 1. What This Gateway Does (Plain Language)
The gateway sits in front of multiple backend services (user, product, investment/funding, order). Every request comes through here first. The gateway:
1. Routes requests to the correct microservice based on the URL path.
2. Lets users log in and get a JWT token (via `/nexus/auth/login`).
3. Checks the JWT token on protected routes (authentication).
4. Applies role / ownership rules (authorization) through custom filters.
5. Adds helpful headers (like user id) so downstream services know who is calling.

If the token is missing, wrong, expired, or you try something you are not allowed to do, the gateway stops the request and returns an error.

---
## 2. Technology Stack
- Spring Boot (Reactive / WebFlux)
- Spring Cloud Gateway
- JJWT (Java JWT library)
- Maven build

This is a fully non-blocking (Reactive) application.

---
## 3. High-Level Request Flow
```
Client -> /nexus/auth/login -> AuthController -> UserService (validate) -> JWT issued
Client -> (other route with Authorization: Bearer <token>)
       -> Global JwtAuthFilter (validates token, extracts user info)
       -> Route-specific Authorization Filter (enforces role/ownership rules)
       -> Downstream Microservice (user/product/funding/order)
       <- Response bubbles back to client
```

---
## 4. Authentication: `AuthController`
Path: `/nexus/auth/login` (POST)
Payload example:
```json
{
  "email": "user@example.com",
  "password": "plainTextPassword"
}
```
Steps performed:
1. Validates the incoming login request (basic structure checks) using `UserValidationUtil`.
2. Calls the User Service (`UserServiceClient`) at the configured URL to verify credentials.
3. Expects the User Service response to include a `data` map containing user fields (id, email, roles, etc.).
4. Extracts: `email` (used as JWT subject), `roles` (list), and `id` (stored as custom claim).
5. Generates a JWT via `JwtUtil.generateToken(email, roles, id)`.
6. Returns a JSON body like:
```json
{
  "token": "<jwt-token>",
  "message": "Login successful"
}
```
Errors:
- 400 if request shape invalid.
- 401 if credentials invalid or user service fails.

---
## 5. JWT Structure & Utility (`JwtUtil`)
Claims stored:
- `sub` (subject): user email
- `roles`: List<String> (e.g. `["SUPPLIER", "FUNDER"]`)
- `id`: user id (String)
- `exp`: expiration timestamp

Validation logic checks expiration only (signature inherently checked when parsing). If expired or malformed parsing fails.

Token Example (decoded claims):
```json
{
  "sub": "user@example.com",
  "roles": ["FUNDER"],
  "id": "64f1b2...",
  "iat": 1732190000,
  "exp": 1732193600
}
```

---
## 6. Authorization Filters (Custom Logic)
All live in `src/main/java/com/nexus/api_gateway/filters`.

### 6.1 Global: `JwtAuthFilter`
Applied as a default filter (see `application.yml`). Responsibilities:
- Skips auth for paths containing `/nexus/auth`, `/actuator/health`, or `/nexus/api/v1/user` (likely public signup/health/user creation).
- Requires `Authorization: Bearer <token>` for other routes.
- Validates the token using `JwtUtil`.
- Extracts email (subject) and user id and adds headers:
  - `X-User-ID` (from email via constants alias)
  - `X-User-Id` (explicit id claim)
- Passes request downstream so other filters/services can use those headers.
Error responses: 401 plain text with a short message.

### 6.2 `UserAuthorizationFilter`
Ownership enforcement for modifying a specific user resource.
- Applies only to `PUT`, `PATCH`, `DELETE` on `/nexus/api/v1/users/{id}` (original) or `/api/v1/users/{id}` (after `StripPrefix=1`).
- Extracts target `{id}` from path.
- Parses JWT again to get `id` claim.
- Compares: authenticated user id == target path id.
- If mismatch -> 403.

### 6.3 `ProductAuthorizationFilter`
Role-based product modification rules.
- Targets paths starting with `/nexus/api/v1/product` or `/nexus/api/v1/products`.
- POST and PUT require role `SUPPLIER`.
- Read-only methods (GET/HEAD/OPTIONS) allowed for any authenticated role.
- Validates token & extracts roles internally (does not rely on headers for roles).

### 6.4 `FundingRequestAuthorizationFilter`
Applied to paths beginning `/api/v1/funding-requests` (after prefix strip). Rules:
- Create funding request (`POST /api/v1/funding-requests`) -> role `FUNDER` only.
- Invest in funding request (`POST /api/v1/funding-requests/{id}/investment`) -> role `INVESTOR` only.
- Distribute returns (`POST /api/v1/funding-requests/{id}/distribute-returns`) -> role `FUNDER` only.
Other methods pass through (still authenticated by global filter).

### 6.5 `OrderAuthorizationFilter`
Targets `/api/v1/orders` or original prefix variant.
Rules:
- `POST /api/v1/orders` -> `FUNDER` only.
- `PUT /api/v1/orders/{orderId}/status` -> `SUPPLIER` or `FUNDER`.
- Any `GET` on orders -> forbidden for `INVESTOR` role.
Logs debug/warn traces (SLF4J) for easier auditing.

### 6.6 Why Each Filter Re-Parses JWT
They parse the token again to reliably get role/id claims without depending on headers that could be spoofed except where those headers are internally set by the gateway itself. Parsing is cheap and keeps logic self-contained.

---
## 7. Routing Configuration (Simplified Explanation)
Defined in `application.yml` under `spring.cloud.gateway.routes`.

Provided configuration (annotated):
```yaml
server:
  port: 8080
jwt:
  algorithm: HS256
  secret: dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWV4YW1wbGUtMTIzNDU2Nzg5MA==  # Base64 encoded key
  expiration: 3600000  # 1 hour in ms
spring:
  main:
    web-application-type: reactive
  cloud:
    gateway:
      default-filters:
        - JwtAuthFilter
      routes:
        - id: user-service
          uri: http://localhost:3000
          predicates:
            - Path=/nexus/api/v1/user, /nexus/api/v1/users/**
          filters:
            - StripPrefix=1
            - UserAuthorizationFilter
        - id: product-service
          uri: http://localhost:3002
          predicates:
            - Path=/nexus/api/v1/product/**, /nexus/api/v1/products/**
          filters:
            - StripPrefix=1
            - ProductAuthorizationFilter
        - id: investment-service
          uri: http://localhost:3004
          predicates:
            - Path=/nexus/api/v1/funding-requests/**
          filters:
            - StripPrefix=1
            - FundingRequestAuthorizationFilter
        - id: order-service
          uri: http://localhost:3007
          predicates:
            - Path=/nexus/api/v1/orders/**
          filters:
            - StripPrefix=1
            - OrderAuthorizationFilter
```

Key points:
- `StripPrefix=1` removes the first path segment (`/nexus`) before forwarding.
  Example: `/nexus/api/v1/orders/123` -> downstream `/api/v1/orders/123`.
- `default-filters` means `JwtAuthFilter` runs for every route unless excluded by internal logic.
- Each route adds one authorization filter tailored to its domain.

---
## 8. Role & Action Matrix (Summary)
| Area | Method & Path Pattern | Required Role(s) | Notes |
|------|-----------------------|------------------|-------|
| User | PUT/PATCH/DELETE /users/{id} | Owner (same id) | Enforced by UserAuthorizationFilter |
| Product | POST/PUT /product(s)** | SUPPLIER | Create/modify only by suppliers |
| Product | GET/HEAD/OPTIONS /product(s)** | Any authenticated | Must pass JWT validation |
| Funding Request | POST /funding-requests | FUNDER | Create funding request |
| Funding Request | POST /funding-requests/{id}/investment | INVESTOR | Invest action |
| Funding Request | POST /funding-requests/{id}/distribute-returns | FUNDER | Distribute returns |
| Order | POST /orders | FUNDER | Create order |
| Order | PUT /orders/{id}/status | SUPPLIER or FUNDER | Update status |
| Order | GET /orders/** | Any except INVESTOR | INVESTOR explicitly forbidden |

(All paths shown after StripPrefix unless stated.)

---
## 9. Running the Gateway
Prerequisites: Java 17+ (depending on pom) & Maven Wrapper included.

Build:
```bash
./mvnw clean package
```
Run:
```bash
./mvnw spring-boot:run
```
Or run the built jar:
```bash
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```
Gateway listens on `http://localhost:8080`.

---
## 10. Example Usage
### 10.1 Login to obtain JWT
```bash
curl -X POST http://localhost:8080/nexus/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"secret"}'
```
Response:
```json
{"token":"<JWT>","message":"Login successful"}
```

### 10.2 Call a protected route
```bash
TOKEN="<JWT>"
curl http://localhost:8080/nexus/api/v1/products/123 \
  -H "Authorization: Bearer $TOKEN"
```

### 10.3 Create product (requires SUPPLIER role)
```bash
curl -X POST http://localhost:8080/nexus/api/v1/product \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Widget","price":19.99}'
```
If missing SUPPLIER role -> 403.

---
## 11. Error Responses (Plain Text)
The filters return simple plain text bodies with HTTP status codes:
- 401 Unauthorized: Missing/invalid token.
- 403 Forbidden: You are authenticated but not allowed to perform the action.
- 400 Bad Request: Malformed path or invalid login request.

Improve later by returning structured JSON (future enhancement).

---
## 12. Security Notes
- JWT secret is Base64 encoded. Replace the sample secret in `application.yml` for production.
- Rotate secrets periodically; put them in environment variables (e.g. `JWT_SECRET`) and reference with `${JWT_SECRET}`.
- Current validation only checks expiration; consider adding audience / issuer claims for tighter security.

---
## 13. Extending / Adding a New Service
Steps to add a new domain (e.g., payments):
1. Create a new authorization filter if special rules apply.
2. Add a new route block in `application.yml` with predicates (Path) and filters (`StripPrefix=1`, custom filter).
3. Reuse `JwtAuthFilter` automatically via `default-filters`.
4. Define role rules clearly in README.

---
## 14. Troubleshooting
| Problem | Cause | Fix |
|---------|-------|-----|
| Always 401 | Missing or malformed Authorization header | Ensure `Authorization: Bearer <token>` |
| 403 on product create | Role not SUPPLIER | Login with user that has SUPPLIER role |
| User update forbidden | Auth user id != path id | Use correct id or adjust ownership rules |
| Gateway not starting | Port conflict | Change `server.port` in config |
| Token rejected | Expired | Re-login to get fresh token |

Enable DEBUG logging for filters if deeper insight is needed (add logging configuration in `application.yml`).

---
## 15. Future Improvements (Suggestions)
- Return JSON error bodies with standardized error codes.
- Centralize role checks to reduce repeated JWT parsing.
- Add rate limiting with Redis using the `X-User-ID` header.
- Integrate OpenAPI documentation for auth endpoint.
- Support refresh tokens.

---
## 16. Quick Glossary
- Authentication: Verifying who you are (token valid?).
- Authorization: Verifying what you can do (role/ownership rules).
- Claims: Data embedded inside the JWT (roles, id, subject, expiration).
- StripPrefix: Gateway filter that removes leading path segment before forwarding to downstream service.

---
## 17. Disclaimer
This README documents current behavior inferred directly from the source code. Adjust as code evolves.

---
## 18. License
See `LICENSE` file for details.
