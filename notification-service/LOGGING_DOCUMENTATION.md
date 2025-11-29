# Consumer Service - Logging Documentation

## Overview

This document describes the comprehensive logging implementation in the Consumer Service for the Nexus Collaborative Supply Chain Platform.

## Logging Framework

- **Framework**: SLF4J with Logback (Spring Boot default)
- **Logger**: `org.slf4j.Logger`
- **Format**: Structured logging with contextual information

## Log Levels

### INFO
Used for:
- Message consumption events from Kafka topics
- Successful email delivery
- Service initialization
- Execution time metrics

### DEBUG
Used for:
- Raw message content from Kafka
- Email preparation details
- SMTP communication details
- Message body previews

### WARN
Used for:
- Missing optional fields (subject, body)
- Validation warnings
- Non-critical issues that don't prevent processing

### ERROR
Used for:
- Failed email delivery (SMTP errors)
- Missing required fields (email, orderId)
- Unexpected exceptions
- Service failures

## Logging Patterns by Component

### 1. ConsumerService

#### Message Consumption (INFO)
```java
log.info("Received email message from Kafka topic: events");
log.info("Received order notification from Kafka topic: orderNotification");
```

#### Message Processing (INFO)
```java
log.info("Processing email - To: {}, Subject: {}", email, subject);
log.info("Processing order notification - OrderId: {}, To: {}, Subject: {}, Timestamp: {}", 
        orderId, email, subject, timestamp);
```

#### Success Logging (INFO)
```java
log.info("Email sent successfully - To: {}, ExecutionTime: {}ms", email, executionTime);
log.info("Order notification sent successfully - OrderId: {}, To: {}, ExecutionTime: {}ms", 
        orderId, email, executionTime);
```

#### Validation Errors (ERROR)
```java
log.error("Invalid email message: email field is missing or empty");
log.error("Invalid order notification: orderId field is missing or empty");
log.error("Invalid order notification: email field is missing or empty - OrderId: {}", orderId);
```

#### Missing Optional Fields (WARN)
```java
log.warn("Email message missing subject field - Using default subject");
log.warn("Email message missing body field - Using empty body");
log.warn("Order notification missing subject - OrderId: {}, Using default", orderId);
log.warn("Order notification missing body - OrderId: {}, Using default", orderId);
```

#### SMTP Failures (ERROR)
```java
log.error("Failed to send email - Error: {}, ExecutionTime: {}ms", e.getMessage(), executionTime, e);
log.error("Failed to send order notification email - Error: {}, ExecutionTime: {}ms", 
        e.getMessage(), executionTime, e);
log.error("SMTP error while sending email - To: {}, Error: {}", to, e.getMessage());
```

#### Unexpected Errors (ERROR)
```java
log.error("Unexpected error processing email message - Error: {}, ExecutionTime: {}ms", 
        e.getMessage(), executionTime, e);
log.error("Unexpected error processing order notification - Error: {}, ExecutionTime: {}ms", 
        e.getMessage(), executionTime, e);
```

#### Debug Logging (DEBUG)
```java
log.debug("Raw message content: {}", message);
log.debug("Raw notification content: {}", message);
log.debug("Email body preview: {}", body.length() > 50 ? body.substring(0, 50) + "..." : body);
log.debug("Notification body preview: {}", body.length() > 50 ? body.substring(0, 50) + "..." : body);
log.debug("Preparing email - From: {}, To: {}, Subject: {}", fromEmail, to, subject);
log.debug("Sending email via JavaMailSender...");
log.debug("Email sent successfully via SMTP");
```

### 2. KafkaConsumerConfig

#### Configuration Initialization (INFO)
```java
log.info("Configuring Kafka ConsumerFactory");
log.info("Kafka Consumer Configuration: Bootstrap Servers: kafka:9092, Group ID: consumer-group, Auto Offset Reset: earliest");
log.info("Configuring Kafka Listener Container Factory");
log.info("Kafka Listener Container Factory configured successfully");
```

#### Debug Configuration (DEBUG)
```java
log.debug("Consumer properties: {}", props);
```

## Log Output Examples

### Successful Email Processing

```
2024-11-21 10:30:45.123 INFO  [consumer-group-0-C-1] o.r.c.s.ConsumerService : Received email message from Kafka topic: events
2024-11-21 10:30:45.125 INFO  [consumer-group-0-C-1] o.r.c.s.ConsumerService : Processing email - To: customer@example.com, Subject: Welcome Email
2024-11-21 10:30:45.126 DEBUG [consumer-group-0-C-1] o.r.c.s.ConsumerService : Preparing email - From: noreply@nexus.com, To: customer@example.com, Subject: Welcome Email
2024-11-21 10:30:45.127 DEBUG [consumer-group-0-C-1] o.r.c.s.ConsumerService : Sending email via JavaMailSender...
2024-11-21 10:30:45.345 DEBUG [consumer-group-0-C-1] o.r.c.s.ConsumerService : Email sent successfully via SMTP
2024-11-21 10:30:45.346 INFO  [consumer-group-0-C-1] o.r.c.s.ConsumerService : Email sent successfully - To: customer@example.com, ExecutionTime: 223ms
```

### Order Notification Processing

```
2024-11-21 10:35:12.456 INFO  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Received order notification from Kafka topic: orderNotification
2024-11-21 10:35:12.458 INFO  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Processing order notification - OrderId: 507f1f77bcf86cd799439011, To: customer@example.com, Subject: Order Confirmed, Timestamp: 2024-11-21T10:35:00Z
2024-11-21 10:35:12.460 DEBUG [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Notification body preview: Your order #12345 has been confirmed and will...
2024-11-21 10:35:12.678 INFO  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Order notification sent successfully - OrderId: 507f1f77bcf86cd799439011, To: customer@example.com, ExecutionTime: 222ms
```

### Missing Required Field Error

```
2024-11-21 10:40:23.789 INFO  [consumer-group-0-C-1] o.r.c.s.ConsumerService : Received email message from Kafka topic: events
2024-11-21 10:40:23.791 ERROR [consumer-group-0-C-1] o.r.c.s.ConsumerService : Invalid email message: email field is missing or empty
```

### SMTP Failure

```
2024-11-21 10:45:34.567 INFO  [consumer-group-0-C-1] o.r.c.s.ConsumerService : Received email message from Kafka topic: events
2024-11-21 10:45:34.569 INFO  [consumer-group-0-C-1] o.r.c.s.ConsumerService : Processing email - To: customer@example.com, Subject: Test Email
2024-11-21 10:45:34.570 DEBUG [consumer-group-0-C-1] o.r.c.s.ConsumerService : Preparing email - From: noreply@nexus.com, To: customer@example.com, Subject: Test Email
2024-11-21 10:45:34.571 DEBUG [consumer-group-0-C-1] o.r.c.s.ConsumerService : Sending email via JavaMailSender...
2024-11-21 10:45:39.572 ERROR [consumer-group-0-C-1] o.r.c.s.ConsumerService : SMTP error while sending email - To: customer@example.com, Error: Could not connect to SMTP host: smtp.gmail.com, port: 587
2024-11-21 10:45:39.573 ERROR [consumer-group-0-C-1] o.r.c.s.ConsumerService : Failed to send email - Error: Could not connect to SMTP host: smtp.gmail.com, port: 587, ExecutionTime: 5006ms
org.springframework.mail.MailSendException: Mail server connection failed
    at org.springframework.mail.javamail.JavaMailSenderImpl.doSend(JavaMailSenderImpl.java:448)
    ...
```

### Missing Optional Fields Warning

```
2024-11-21 10:50:45.123 INFO  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Received order notification from Kafka topic: orderNotification
2024-11-21 10:50:45.125 WARN  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Order notification missing subject - OrderId: 507f1f77bcf86cd799439011, Using default
2024-11-21 10:50:45.127 INFO  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Processing order notification - OrderId: 507f1f77bcf86cd799439011, To: customer@example.com, Subject: Order Notification, Timestamp: 2024-11-21T10:50:00Z
2024-11-21 10:50:45.345 INFO  [order-notification-group-0-C-1] o.r.c.s.ConsumerService : Order notification sent successfully - OrderId: 507f1f77bcf86cd799439011, To: customer@example.com, ExecutionTime: 222ms
```

## Configuration

### application.properties

```properties
# Root logging level
logging.level.root=INFO

# Consumer service logging
logging.level.org.razz.consumer=INFO
logging.level.org.razz.consumer.service.ConsumerService=INFO
logging.level.org.razz.consumer.config.KafkaConsumerConfig=INFO

# Spring Kafka logging (reduce noise)
logging.level.org.springframework.kafka=WARN
logging.level.org.apache.kafka=WARN

# Spring Mail logging (for debugging)
logging.level.org.springframework.mail=INFO

# Enable debug for troubleshooting
# logging.level.org.razz.consumer=DEBUG
# logging.level.org.springframework.mail=DEBUG

# Log pattern
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} : %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} : %msg%n

# Log file configuration (optional)
logging.file.name=logs/consumer-service.log
logging.file.max-size=10MB
logging.file.max-history=30
```

### Logback Configuration (Optional)

Create `src/main/resources/logback-spring.xml` for advanced configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} : %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/consumer-service.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} : %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/consumer-service-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>
    
    <logger name="org.razz.consumer" level="INFO"/>
    <logger name="org.springframework.kafka" level="WARN"/>
    <logger name="org.apache.kafka" level="WARN"/>
    <logger name="org.springframework.mail" level="INFO"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Monitoring and Analysis

### Key Metrics to Monitor

1. **Message Processing Rate**: Track INFO logs for "Received email message" and "Received order notification"
2. **Email Success Rate**: Compare "sent successfully" vs "Failed to send" ERROR logs
3. **Execution Time**: Monitor execution time in success logs (should be < 1000ms typically)
4. **Error Rate**: Track ERROR level logs for SMTP failures and validation errors
5. **Missing Fields**: Track WARN logs for missing optional fields

### Log Analysis Queries

#### Count successful emails sent
```bash
grep "Email sent successfully" consumer-service.log | wc -l
```

#### Count SMTP failures
```bash
grep "SMTP error while sending email" consumer-service.log | wc -l
```

#### Average execution time
```bash
grep "ExecutionTime:" consumer-service.log | awk -F'ExecutionTime: ' '{print $2}' | awk -F'ms' '{print $1}' | awk '{sum+=$1; count++} END {print sum/count}'
```

#### Count messages by topic
```bash
# Events topic
grep "Received email message from Kafka topic: events" consumer-service.log | wc -l

# Order notification topic
grep "Received order notification from Kafka topic: orderNotification" consumer-service.log | wc -l
```

## Best Practices

1. **Use Parameterized Logging**: Always use `{}` placeholders for variables
   ```java
   // Good
   log.info("Email sent to: {}", email);
   
   // Bad (string concatenation)
   log.info("Email sent to: " + email);
   ```

2. **Include Execution Time**: Always log execution time for performance monitoring
   ```java
   long startTime = System.currentTimeMillis();
   // ... processing ...
   long executionTime = System.currentTimeMillis() - startTime;
   log.info("Operation completed - ExecutionTime: {}ms", executionTime);
   ```

3. **Log Contextual Information**: Include relevant IDs and context
   ```java
   log.info("Processing order notification - OrderId: {}, To: {}", orderId, email);
   ```

4. **Use Appropriate Log Levels**: Don't log sensitive data at INFO level
   ```java
   log.debug("Raw message content: {}", message); // OK for DEBUG
   log.info("Processing email - To: {}", email); // Don't include full message body
   ```

5. **Include Stack Traces for Errors**: Always include exception in ERROR logs
   ```java
   log.error("Failed to send email - Error: {}", e.getMessage(), e); // Include exception
   ```

## Troubleshooting Guide

### High Email Failure Rate
1. Check ERROR logs for SMTP errors
2. Verify SMTP configuration in application.properties
3. Test SMTP connectivity manually
4. Check email credentials and authentication

### Slow Email Processing
1. Check execution times in INFO logs
2. Look for SMTP timeout issues in DEBUG logs
3. Verify SMTP server performance
4. Consider connection pooling optimization

### Missing Messages
1. Check Kafka consumer group lag
2. Verify Kafka topic configuration
3. Check for deserialization errors in ERROR logs
4. Review auto-offset-reset configuration

## Related Documentation

- [README.md](README.md) - Main service documentation
- [UNIT_TEST_DOCUMENTATION.md](UNIT_TEST_DOCUMENTATION.md) - Testing guidelines
