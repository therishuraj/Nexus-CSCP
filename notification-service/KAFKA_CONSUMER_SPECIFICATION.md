# =============================================================================
# CONSUMER SERVICE - KAFKA CONSUMER SPECIFICATION
# =============================================================================
# Business Context: Nexus Collaborative Supply Chain Platform
# Service: Email Notification Consumer
# Version: 1.0.0
# Last Updated: November 2024
# 
# STAKEHOLDER INFORMATION:
# - Business Owner: Communications Department
# - Technical Contact: Backend Engineering Team (backend-support@nexus.com)
# - Primary Stakeholders: Customer Communications Team, Order Management Team
# - Secondary Stakeholders: Infrastructure Team, Monitoring Team
# - Security Stakeholders: Security Operations Team, Email Security Team
# - Infrastructure Stakeholders: DevOps Team, Kafka Team
# =============================================================================

# SERVICE OVERVIEW

## Purpose
The Consumer Service is a Kafka consumer microservice that processes email notification 
events from multiple Kafka topics and delivers emails to users via SMTP.

## Architecture Pattern
- **Pattern**: Event-Driven Architecture (Consumer)
- **Messaging**: Apache Kafka
- **Delivery**: SMTP Email

## Business Value
- Decouples email sending from business logic services
- Provides reliable email delivery with Kafka's durability guarantees
- Centralizes email sending configuration and monitoring
- Enables asynchronous notification processing

# =============================================================================
# KAFKA TOPICS CONSUMED
# =============================================================================

## Topic 1: events

### Business Context
Generic email notifications for various platform events including user registration,
password resets, account updates, and system notifications.

### Consumer Configuration
- **Topic Name**: `events`
- **Consumer Group**: `consumer-group`
- **Partition Assignment**: Auto (Kafka balancing)
- **Offset Strategy**: Earliest (processes all messages from beginning)
- **Concurrency**: 3 concurrent consumers

### Message Schema

#### JSON Structure
```json
{
  "email": "string (required)",
  "subject": "string (optional, default: 'Notification')",
  "body": "string (optional, default: '')"
}
```

#### Field Specifications

| Field | Type | Required | Constraints | Default | Description |
|-------|------|----------|-------------|---------|-------------|
| `email` | String | Yes | Valid email format, non-empty | - | Recipient email address |
| `subject` | String | No | Max 200 characters | "Notification" | Email subject line |
| `body` | String | No | Max 10,000 characters | "" | Email body content (plain text) |

#### Example Messages

**Complete Message**:
```json
{
  "email": "customer@example.com",
  "subject": "Welcome to Nexus Platform",
  "body": "Thank you for joining Nexus. Your account has been created successfully."
}
```

**Minimal Message** (uses defaults):
```json
{
  "email": "user@example.com"
}
```

**User Registration Example**:
```json
{
  "email": "newuser@example.com",
  "subject": "Account Created Successfully",
  "body": "Dear User,\n\nYour Nexus account has been created. You can now log in with your credentials.\n\nBest regards,\nNexus Team"
}
```

**Password Reset Example**:
```json
{
  "email": "user@example.com",
  "subject": "Password Reset Request",
  "body": "We received a request to reset your password. Click the link below to reset:\n\nhttps://nexus.com/reset?token=xyz123"
}
```

### Processing Behavior

1. **Validation**:
   - Checks if `email` field exists and is not empty/whitespace
   - If email is missing/invalid: Logs error and skips processing
   - If subject is missing: Uses default "Notification"
   - If body is missing: Uses empty string

2. **Email Sending**:
   - Creates SimpleMailMessage with validated fields
   - Sends via configured SMTP server
   - Logs execution time for monitoring

3. **Error Handling**:
   - SMTP failures are logged but don't stop consumer
   - Consumer continues processing next messages
   - Failed messages are not automatically retried (implement DLQ if needed)

4. **Logging**:
   - INFO: Message received, processing started, success/failure
   - DEBUG: Raw message content, email details
   - WARN: Missing optional fields
   - ERROR: Missing email, SMTP failures, unexpected exceptions

---

## Topic 2: orderNotification

### Business Context
Order-specific email notifications sent when orders are placed, confirmed, shipped,
or updated. Provides customers with real-time order status updates.

### Consumer Configuration
- **Topic Name**: `orderNotification`
- **Consumer Group**: `order-notification-group`
- **Partition Assignment**: Auto (Kafka balancing)
- **Offset Strategy**: Earliest (ensures no order notifications are missed)
- **Concurrency**: 3 concurrent consumers

### Message Schema

#### JSON Structure
```json
{
  "orderId": "string (required)",
  "email": "string (required)",
  "subject": "string (optional, default: 'Order Notification')",
  "body": "string (optional, default: 'Your order has been processed.')",
  "timestamp": "string (optional, ISO-8601 format)"
}
```

#### Field Specifications

| Field | Type | Required | Constraints | Default | Description |
|-------|------|----------|-------------|---------|-------------|
| `orderId` | String | Yes | MongoDB ObjectId format, non-empty | - | Unique order identifier |
| `email` | String | Yes | Valid email format, non-empty | - | Customer email address |
| `subject` | String | No | Max 200 characters | "Order Notification" | Email subject line |
| `body` | String | No | Max 10,000 characters | "Your order has been processed." | Email body content |
| `timestamp` | String | No | ISO-8601 format | - | Order event timestamp (for logging) |

#### Example Messages

**Order Placed**:
```json
{
  "orderId": "507f1f77bcf86cd799439011",
  "email": "customer@example.com",
  "subject": "Order #12345 Confirmed",
  "body": "Dear Customer,\n\nThank you for your order. Your order #12345 has been confirmed and is being processed.\n\nOrder Details:\n- Product: Industrial Steel Beam\n- Quantity: 10 units\n- Total: $15,000.00\n\nWe'll notify you when your order ships.",
  "timestamp": "2024-11-21T10:30:45.123Z"
}
```

**Order Shipped**:
```json
{
  "orderId": "507f1f77bcf86cd799439011",
  "email": "customer@example.com",
  "subject": "Order #12345 Shipped",
  "body": "Good news! Your order #12345 has been shipped.\n\nTracking Number: TRK123456789\n\nEstimated Delivery: November 25, 2024",
  "timestamp": "2024-11-21T14:20:30.456Z"
}
```

**Order Delivered**:
```json
{
  "orderId": "507f1f77bcf86cd799439011",
  "email": "customer@example.com",
  "subject": "Order #12345 Delivered",
  "body": "Your order #12345 has been delivered successfully.\n\nThank you for choosing Nexus!",
  "timestamp": "2024-11-25T09:15:00.789Z"
}
```

**Minimal Order Notification** (uses defaults):
```json
{
  "orderId": "507f1f77bcf86cd799439012",
  "email": "customer@example.com"
}
```

### Processing Behavior

1. **Validation**:
   - Checks if `orderId` exists and is not empty
   - Checks if `email` exists and is not empty
   - If orderId or email is missing/invalid: Logs error and skips processing
   - If subject is missing: Uses default "Order Notification"
   - If body is missing: Uses default "Your order has been processed."
   - Timestamp is optional and used only for logging

2. **Email Sending**:
   - Creates SimpleMailMessage with order-specific content
   - Sends via configured SMTP server
   - Logs orderId for traceability

3. **Error Handling**:
   - SMTP failures are logged with orderId for troubleshooting
   - Consumer continues processing next messages
   - Failed messages can be tracked via orderId in logs

4. **Logging**:
   - INFO: Notification received, orderId, email, processing result
   - DEBUG: Full notification content, timestamp
   - WARN: Missing optional fields
   - ERROR: Missing required fields (orderId/email), SMTP failures

---

# =============================================================================
# PRODUCER INTEGRATION GUIDE
# =============================================================================

## For Services Publishing to "events" Topic

### Spring Kafka Producer Example

```java
@Service
public class NotificationProducer {
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public void sendEmailNotification(String email, String subject, String body) {
        Map<String, Object> message = Map.of(
            "email", email,
            "subject", subject,
            "body", body
        );
        kafkaTemplate.send("events", message);
    }
}
```

### Usage Examples

```java
// Welcome email
notificationProducer.sendEmailNotification(
    "newuser@example.com",
    "Welcome to Nexus",
    "Thank you for joining our platform!"
);

// Password reset
notificationProducer.sendEmailNotification(
    "user@example.com",
    "Password Reset Request",
    "Click here to reset your password: " + resetLink
);
```

---

## For Services Publishing to "orderNotification" Topic

### Spring Kafka Producer Example

```java
@Service
public class OrderNotificationProducer {
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public void sendOrderNotification(String orderId, String email, 
                                     String subject, String body) {
        Map<String, Object> message = Map.of(
            "orderId", orderId,
            "email", email,
            "subject", subject,
            "body", body,
            "timestamp", Instant.now().toString()
        );
        kafkaTemplate.send("orderNotification", message);
    }
}
```

### Usage Example (Order Service)

```java
// After order placement
orderNotificationProducer.sendOrderNotification(
    order.getId(),
    customer.getEmail(),
    "Order #" + orderNumber + " Confirmed",
    buildOrderConfirmationBody(order)
);

// After order shipment
orderNotificationProducer.sendOrderNotification(
    order.getId(),
    customer.getEmail(),
    "Order #" + orderNumber + " Shipped",
    buildShipmentNotificationBody(order, trackingNumber)
);
```

---

# =============================================================================
# MONITORING & OBSERVABILITY
# =============================================================================

## Key Metrics to Monitor

### 1. Message Processing Metrics
- **Messages Consumed/Second**: Rate of Kafka message consumption
- **Processing Time**: Time from consumption to email sent
- **Success Rate**: Percentage of successfully sent emails
- **Failure Rate**: Percentage of SMTP failures

### 2. Email Delivery Metrics
- **Emails Sent**: Total count of successfully sent emails
- **SMTP Failures**: Count of failed email attempts
- **Average Execution Time**: Average time to send email

### 3. Kafka Consumer Metrics
- **Consumer Lag**: Difference between latest offset and consumed offset
- **Partition Assignment**: Number of partitions assigned to consumer
- **Rebalance Events**: Frequency of consumer group rebalances

## Health Check Endpoints

### Spring Boot Actuator

```bash
# Health check
curl http://consumer-service:8085/actuator/health

# Kafka health
curl http://consumer-service:8085/actuator/health/kafka

# Mail health
curl http://consumer-service:8085/actuator/health/mail

# Metrics
curl http://consumer-service:8085/actuator/metrics
```

### Expected Health Response

```json
{
  "status": "UP",
  "components": {
    "kafka": {
      "status": "UP",
      "details": {
        "clusterId": "kafka-cluster-1",
        "nodes": 1
      }
    },
    "mail": {
      "status": "UP",
      "details": {
        "location": "smtp.gmail.com:587"
      }
    }
  }
}
```

---

# =============================================================================
# ERROR SCENARIOS & HANDLING
# =============================================================================

## Validation Errors

| Scenario | Error Handling | Recovery |
|----------|----------------|----------|
| Missing email field | Log ERROR, skip message | Consumer continues |
| Empty email field | Log ERROR, skip message | Consumer continues |
| Missing orderId (order topic) | Log ERROR, skip message | Consumer continues |
| Invalid JSON format | Log ERROR, skip message | Consumer continues |

## SMTP Failures

| Scenario | Error Handling | Recovery |
|----------|----------------|----------|
| Authentication failure | Log ERROR with details | Check credentials |
| Connection timeout | Log ERROR, continue | Check SMTP server |
| Invalid recipient | Log ERROR, continue | Validate email format |
| Rate limit exceeded | Log ERROR, continue | Implement rate limiting |

## Kafka Issues

| Scenario | Error Handling | Recovery |
|----------|----------------|----------|
| Broker unavailable | Automatic retry (Spring Kafka) | Wait for broker |
| Deserialization error | Log ERROR, skip message | Fix message format |
| Consumer lag high | Alert/monitoring | Scale consumers |
| Rebalance triggered | Automatic (Spring Kafka) | Normal operation |

---

# =============================================================================
# PERFORMANCE & SCALABILITY
# =============================================================================

## Current Configuration

- **Concurrent Consumers**: 3 per topic
- **Batch Size**: Default (500 records)
- **SMTP Timeout**: 5 seconds
- **Auto Commit Interval**: 1 second

## Scaling Recommendations

### Horizontal Scaling
```bash
# Deploy multiple consumer instances
docker-compose up --scale consumer-service=3
```

### Vertical Scaling (Concurrency)
```properties
# Increase concurrent listeners
spring.kafka.listener.concurrency=10
```

### Performance Tuning
```properties
# Increase batch size for higher throughput
spring.kafka.consumer.max-poll-records=1000

# Reduce commit interval for faster offset updates
spring.kafka.consumer.auto-commit-interval=500

# Connection pooling for SMTP
spring.mail.properties.mail.smtp.connectionpool.size=10
```

---

# =============================================================================
# SECURITY CONSIDERATIONS
# =============================================================================

## Email Security

1. **Credentials**: Store SMTP credentials in environment variables or secrets manager
2. **TLS/SSL**: Always use STARTTLS or SSL for SMTP connections
3. **Authentication**: Enable SMTP authentication
4. **Rate Limiting**: Implement rate limiting to prevent spam

## Kafka Security

1. **Authentication**: Use SASL/SCRAM or SSL for Kafka authentication
2. **Authorization**: Configure Kafka ACLs for topic access
3. **Encryption**: Enable SSL for in-transit encryption
4. **Consumer Groups**: Isolate consumer groups by environment

## Data Privacy

1. **PII Handling**: Email addresses are logged at INFO level (monitor access)
2. **Message Content**: Email bodies logged at DEBUG level only
3. **Retention**: Configure log retention policies
4. **Compliance**: Follow GDPR/privacy regulations for email content

---

# =============================================================================
# TROUBLESHOOTING GUIDE
# =============================================================================

## Common Issues

### Issue: Emails Not Sending

**Symptoms**: Messages consumed but no emails delivered

**Check**:
1. SMTP configuration in environment variables
2. SMTP server connectivity: `telnet smtp.gmail.com 587`
3. Email credentials validity
4. Logs for SMTP errors

### Issue: High Consumer Lag

**Symptoms**: orderNotification lag increasing

**Check**:
1. Consumer processing time (check execution time logs)
2. SMTP server performance
3. Number of concurrent consumers
4. Kafka partition count vs consumer count

### Issue: Messages Skipped

**Symptoms**: Missing orderId or email in logs

**Check**:
1. Producer message format (must include required fields)
2. JSON serialization in producer
3. Consumer deserialization configuration

---

# =============================================================================
# RELATED DOCUMENTATION
# =============================================================================

- [README.md](README.md) - Service overview and setup guide
- [LOGGING_DOCUMENTATION.md](LOGGING_DOCUMENTATION.md) - Detailed logging patterns
- [UNIT_TEST_DOCUMENTATION.md](UNIT_TEST_DOCUMENTATION.md) - Testing strategy

---

# =============================================================================
# SUPPORT & CONTACT
# =============================================================================

**For Issues or Questions**:
- Email: backend-support@nexus.com
- Team: Backend Engineering Team
- Owner: Infrastructure Team

**Escalation**:
- Kafka Issues: kafka-team@nexus.com
- Email Delivery Issues: email-ops@nexus.com
- Security Concerns: security@nexus.com

---

# =============================================================================
# VERSION HISTORY
# =============================================================================

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0.0 | 2024-11-21 | Initial documentation | Backend Team |

---

# =============================================================================
# END OF KAFKA CONSUMER SPECIFICATION
# =============================================================================
