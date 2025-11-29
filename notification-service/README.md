# Consumer Service

Email notification consumer service for the Nexus Collaborative Supply Chain Platform.

## Overview

The Consumer Service is a Kafka-based microservice that consumes messages from multiple Kafka topics and sends email notifications to users. It acts as the notification delivery system for the entire Nexus platform, handling both generic email notifications and order-specific notifications.

## Features

- **Kafka Message Consumption**: Consumes messages from multiple Kafka topics
  - `events`: Generic email notifications
  - `orderNotification`: Order-related email notifications
- **Email Delivery**: Sends emails via SMTP using JavaMailSender
- **Resilient Design**: Graceful error handling for failed email deliveries
- **Comprehensive Logging**: Detailed logging for monitoring and debugging
- **Input Validation**: Validates all incoming message fields before processing
- **Default Values**: Provides sensible defaults for missing optional fields

## Technology Stack

- **Spring Boot**: 3.5.7
- **Java**: 17
- **Apache Kafka**: Message streaming platform
- **Spring Kafka**: Kafka integration for Spring
- **JavaMail**: Email sending via SMTP
- **Spring Boot Actuator**: Health checks and monitoring
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for tests

## Architecture

### Kafka Topics

The service consumes from two Kafka topics:

#### 1. `events` Topic
- **Purpose**: Generic email notifications
- **Consumer Group**: `consumer-group`
- **Message Format**:
```json
{
  "email": "recipient@example.com",
  "subject": "Email Subject",
  "body": "Email body content"
}
```

#### 2. `orderNotification` Topic
- **Purpose**: Order-related email notifications
- **Consumer Group**: `order-notification-group`
- **Message Format**:
```json
{
  "orderId": "507f1f77bcf86cd799439011",
  "email": "customer@example.com",
  "subject": "Order Confirmation",
  "body": "Your order has been placed successfully",
  "timestamp": "2024-11-21T10:30:00Z"
}
```

### Configuration

The service uses the following Kafka configuration:
- **Bootstrap Servers**: `kafka:9092`
- **Auto Offset Reset**: `earliest` (processes all messages from the beginning)
- **Key Deserializer**: String
- **Value Deserializer**: JSON (deserializes to HashMap)
- **Trusted Packages**: `*` (allows all packages for deserialization)

## Installation & Setup

### Prerequisites

1. **Java 17** or higher installed
2. **Maven 3.6+** installed
3. **Apache Kafka** running on `kafka:9092`
4. **SMTP Server** configured (Gmail, SendGrid, or custom SMTP)

### Environment Variables

Configure the following environment variables for email sending:

```bash
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

### Build & Run

#### Using Maven

```bash
# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

#### Using Docker

```bash
# Build Docker image
docker build -t consumer-service .

# Run container
docker run -d \
  -e SPRING_MAIL_HOST=smtp.gmail.com \
  -e SPRING_MAIL_PORT=587 \
  -e SPRING_MAIL_USERNAME=your-email@gmail.com \
  -e SPRING_MAIL_PASSWORD=your-app-password \
  --name consumer-service \
  consumer-service
```

#### Using Docker Compose

```bash
# Start all services including consumer
docker-compose up -d
```

## Configuration Files

### application.properties (Example)

Create `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8085
spring.application.name=consumer-service

# Kafka Configuration
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.consumer.group-id=consumer-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*

# Email Configuration (Gmail Example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Logging Configuration
logging.level.org.razz.consumer=INFO
logging.level.org.springframework.kafka=WARN
logging.level.org.springframework.mail=DEBUG
```

## Usage

### Publishing Messages to Kafka

#### Generic Email (events topic)

Using Kafka console producer:
```bash
kafka-console-producer --broker-list kafka:9092 --topic events

# Enter message:
{"email":"recipient@example.com","subject":"Test Email","body":"This is a test email"}
```

Using Spring Kafka (from another service):
```java
kafkaTemplate.send("events", Map.of(
    "email", "recipient@example.com",
    "subject", "Test Email",
    "body", "This is a test email"
));
```

#### Order Notification (orderNotification topic)

```bash
kafka-console-producer --broker-list kafka:9092 --topic orderNotification

# Enter message:
{"orderId":"507f1f77bcf86cd799439011","email":"customer@example.com","subject":"Order Confirmed","body":"Your order has been confirmed","timestamp":"2024-11-21T10:30:00Z"}
```

### Message Processing Flow

1. **Message Received**: Kafka consumer receives message from topic
2. **Validation**: Service validates required fields (email, orderId for order notifications)
3. **Default Values**: Applies defaults for missing optional fields (subject, body)
4. **Email Preparation**: Creates SimpleMailMessage with validated data
5. **Email Sending**: Sends email via configured SMTP server
6. **Logging**: Logs success or failure with execution time

## Validation Rules

### Generic Email Messages

- **Required Fields**:
  - `email`: Must be present and non-empty
- **Optional Fields**:
  - `subject`: Defaults to "Notification" if missing
  - `body`: Defaults to empty string if missing

### Order Notification Messages

- **Required Fields**:
  - `orderId`: Must be present and non-empty
  - `email`: Must be present and non-empty
- **Optional Fields**:
  - `subject`: Defaults to "Order Notification" if missing
  - `body`: Defaults to "Your order has been processed." if missing
  - `timestamp`: Optional, used for logging only

## Error Handling

The service implements robust error handling:

1. **Missing Required Fields**: Logs error and skips email sending
2. **SMTP Failures**: Logs error with stack trace, does not crash service
3. **Invalid Message Format**: Catches exceptions and logs details
4. **Kafka Connection Issues**: Spring Kafka handles retries automatically

## Logging

### Log Levels

- **INFO**: Message consumption, email sending success, execution times
- **DEBUG**: Detailed message content, SMTP communication
- **WARN**: Missing optional fields, validation warnings
- **ERROR**: Failed email delivery, SMTP errors, unexpected exceptions

### Example Log Output

```
2024-11-21 10:30:45 INFO  ConsumerService - Received email message from Kafka topic: events
2024-11-21 10:30:45 INFO  ConsumerService - Processing email - To: recipient@example.com, Subject: Test Email
2024-11-21 10:30:45 DEBUG ConsumerService - Preparing email - From: noreply@nexus.com, To: recipient@example.com, Subject: Test Email
2024-11-21 10:30:45 DEBUG ConsumerService - Sending email via JavaMailSender...
2024-11-21 10:30:46 INFO  ConsumerService - Email sent successfully - To: recipient@example.com, ExecutionTime: 245ms
```

## Testing

### Run Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ConsumerServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage

The service includes comprehensive unit tests covering:
- ✅ Valid email message consumption
- ✅ Order notification consumption
- ✅ Missing required fields validation
- ✅ Missing optional fields (default values)
- ✅ SMTP failure handling
- ✅ Kafka configuration validation

**Current Coverage**: ~85% (15 test cases)

## Monitoring & Health Checks

### Actuator Endpoints

The service exposes Spring Boot Actuator endpoints:

- **Health Check**: `GET http://localhost:8085/actuator/health`
- **Info**: `GET http://localhost:8085/actuator/info`
- **Metrics**: `GET http://localhost:8085/actuator/metrics`

### Health Check Response

```json
{
  "status": "UP",
  "components": {
    "mail": {
      "status": "UP",
      "details": {
        "location": "smtp.gmail.com:587"
      }
    },
    "kafka": {
      "status": "UP"
    }
  }
}
```

## Troubleshooting

### Email Not Sending

1. **Check SMTP Configuration**:
   ```bash
   # Verify environment variables
   echo $SPRING_MAIL_USERNAME
   echo $SPRING_MAIL_PASSWORD
   ```

2. **Enable Debug Logging**:
   ```properties
   logging.level.org.springframework.mail=DEBUG
   logging.level.org.razz.consumer=DEBUG
   ```

3. **Test SMTP Connection**:
   ```bash
   telnet smtp.gmail.com 587
   ```

### Kafka Connection Issues

1. **Verify Kafka is Running**:
   ```bash
   docker ps | grep kafka
   ```

2. **Check Kafka Topics**:
   ```bash
   kafka-topics --bootstrap-server kafka:9092 --list
   ```

3. **Test Consumer Group**:
   ```bash
   kafka-consumer-groups --bootstrap-server kafka:9092 --describe --group consumer-group
   ```

### Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `MailAuthenticationException` | Invalid SMTP credentials | Verify username/password in environment variables |
| `MailSendException` | SMTP server unreachable | Check SMTP host and port configuration |
| `ConsumerAuthorizationException` | Kafka ACL issues | Verify Kafka permissions for consumer group |
| `SerializationException` | Invalid JSON message | Ensure messages follow the correct JSON schema |

## Performance Considerations

- **Concurrent Processing**: Kafka listener uses concurrent container factory
- **Batch Size**: Default batch size is 500 records
- **Email Timeout**: Default SMTP timeout is 5 seconds
- **Retry Policy**: Failed emails are logged but not retried (implement retry logic if needed)

### Scaling

To scale the consumer service:

```bash
# Increase number of concurrent consumers
spring.kafka.listener.concurrency=5

# Increase thread pool size
spring.task.execution.pool.core-size=10
```

## Security Considerations

1. **Email Credentials**: Store in environment variables or secrets manager
2. **SMTP TLS/SSL**: Always use encrypted connection (STARTTLS or SSL)
3. **Kafka Authentication**: Enable SASL authentication in production
4. **Message Validation**: Validate all input fields before processing
5. **Rate Limiting**: Implement rate limiting for email sending to prevent spam

## Integration with Other Services

### Producer Service
- Publishes generic email events to `events` topic

### Order Service
- Publishes order notifications to `orderNotification` topic
- Includes order details in message payload

### User Service
- Provides user email addresses for notifications

## Docker Configuration

### Dockerfile

The service includes a multi-stage Dockerfile for optimized builds:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Related Documentation

- [LOGGING_DOCUMENTATION.md](LOGGING_DOCUMENTATION.md) - Detailed logging guidelines
- [UNIT_TEST_DOCUMENTATION.md](UNIT_TEST_DOCUMENTATION.md) - Test coverage and strategy
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/reference/html/)
- [JavaMail API](https://javaee.github.io/javamail/)

## Contributing

When contributing to the Consumer Service:

1. Add unit tests for new features
2. Update logging for new operations
3. Follow existing code style and patterns
4. Update this README for configuration changes
5. Test email delivery with real SMTP server

## Support

For issues or questions:
- **Email**: backend-support@nexus.com
- **Team**: Backend Engineering Team
- **Owner**: Infrastructure Team

## License

Copyright © 2024 Nexus Platform. All rights reserved.
