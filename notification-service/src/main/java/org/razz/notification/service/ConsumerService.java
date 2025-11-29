package org.razz.notification.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer Service for Email Notifications
 * 
 * This service consumes messages from Kafka topics and sends email notifications.
 * It handles two types of events:
 * 1. Generic events from "events" topic
 * 2. Order notifications from "orderNotification" topic
 * 
 * @author Backend Engineering Team
 */
@Service
public class ConsumerService {
    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ConsumerService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        log.info("ConsumerService initialized with email sender");
    }

    /**
     * Consumes generic email messages from "events" topic
     * 
     * @param message Map containing email, subject, and body
     */
    @KafkaListener(topics = "events", groupId = "consumer-group")
    public void consume(Map<String, Object> message) {
        long startTime = System.currentTimeMillis();
        log.info("Received email message from Kafka topic: events");
        log.debug("Raw message content: {}", message);

        try {
            // Extract message fields
            String email = (String) message.get("email");
            String subject = (String) message.get("subject");
            String body = (String) message.get("body");

            // Validate required fields
            if (email == null || email.trim().isEmpty()) {
                log.error("Invalid email message: email field is missing or empty");
                return;
            }
            if (subject == null || subject.trim().isEmpty()) {
                log.warn("Email message missing subject field - Using default subject");
                subject = "Notification";
            }
            if (body == null || body.trim().isEmpty()) {
                log.warn("Email message missing body field - Using empty body");
                body = "";
            }

            log.info("Processing email - To: {}, Subject: {}", email, subject);
            log.debug("Email body preview: {}", body.length() > 50 ? body.substring(0, 50) + "..." : body);

            sendEmail(email, subject, body);
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Email sent successfully - To: {}, ExecutionTime: {}ms", email, executionTime);
            
        } catch (MailException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Failed to send email - Error: {}, ExecutionTime: {}ms", e.getMessage(), executionTime, e);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Unexpected error processing email message - Error: {}, ExecutionTime: {}ms", 
                    e.getMessage(), executionTime, e);
        }
    }
    
    /**
     * Consumes order notification messages from "orderNotification" topic
     * 
     * @param message Map containing orderId, email, subject, body, and timestamp
     */
    @KafkaListener(topics = "orderNotification", groupId = "order-notification-group")
    public void consumeOrderNotification(Map<String, Object> message) {
        long startTime = System.currentTimeMillis();
        log.info("Received order notification from Kafka topic: orderNotification");
        log.debug("Raw notification content: {}", message);

        try {
            // Extract message fields
            String orderId = (String) message.get("orderId");
            String email = (String) message.get("email");
            String subject = (String) message.get("subject");
            String body = (String) message.get("body");
            String timestamp = (String) message.get("timestamp");

            // Validate required fields
            if (orderId == null || orderId.trim().isEmpty()) {
                log.error("Invalid order notification: orderId field is missing or empty");
                return;
            }
            if (email == null || email.trim().isEmpty()) {
                log.error("Invalid order notification: email field is missing or empty - OrderId: {}", orderId);
                return;
            }
            if (subject == null || subject.trim().isEmpty()) {
                log.warn("Order notification missing subject - OrderId: {}, Using default", orderId);
                subject = "Order Notification";
            }
            if (body == null || body.trim().isEmpty()) {
                log.warn("Order notification missing body - OrderId: {}, Using default", orderId);
                body = "Your order has been processed.";
            }

            log.info("Processing order notification - OrderId: {}, To: {}, Subject: {}, Timestamp: {}", 
                    orderId, email, subject, timestamp);
            log.debug("Notification body preview: {}", body.length() > 50 ? body.substring(0, 50) + "..." : body);

            sendEmail(email, subject, body);
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Order notification sent successfully - OrderId: {}, To: {}, ExecutionTime: {}ms", 
                    orderId, email, executionTime);
            
        } catch (MailException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Failed to send order notification email - Error: {}, ExecutionTime: {}ms", 
                    e.getMessage(), executionTime, e);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Unexpected error processing order notification - Error: {}, ExecutionTime: {}ms", 
                    e.getMessage(), executionTime, e);
        }
    }

    /**
     * Sends an email using JavaMailSender
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body content
     * @throws MailException if email sending fails
     */
    private void sendEmail(String to, String subject, String body) {
        log.debug("Preparing email - From: {}, To: {}, Subject: {}", fromEmail, to, subject);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            log.debug("Sending email via JavaMailSender...");
            mailSender.send(message);
            log.debug("Email sent successfully via SMTP");
            
        } catch (MailException e) {
            log.error("SMTP error while sending email - To: {}, Error: {}", to, e.getMessage());
            throw e;
        }
    }
}