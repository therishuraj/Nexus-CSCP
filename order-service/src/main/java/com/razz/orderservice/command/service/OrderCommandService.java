package com.razz.orderservice.command.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.razz.orderservice.client.InvestmentServiceClient;
import com.razz.orderservice.client.ProductServiceClient;
import com.razz.orderservice.client.UserServiceClient;
import com.razz.orderservice.dto.FundingRequestResponse;
import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.dto.ProductResponse;
import com.razz.orderservice.dto.UserEmailResponse;
import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.model.write.Order;
import com.razz.orderservice.repository.OrderRepository;
import com.razz.orderservice.repository.OrderViewRepository;

@Service
public class OrderCommandService {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandService.class);
    
    private final OrderRepository repo;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final InvestmentServiceClient investmentServiceClient;
    private final OrderViewRepository orderViewRepository;
    
    @Value("${admin.user.id:691f333917c065b20466799d}")
    private String adminUserId;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public OrderCommandService(OrderRepository repo, ProductServiceClient productServiceClient, 
                                UserServiceClient userServiceClient, InvestmentServiceClient investmentServiceClient,
                                OrderViewRepository orderViewRepository) {
        this.repo = repo;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
        this.investmentServiceClient = investmentServiceClient;
        this.orderViewRepository = orderViewRepository;
    }

    public String place(PlaceOrderRequest cmd) {
        log.info("Placing order - ProductId: {}, Quantity: {}, FunderId: {}, SupplierId: {}, RequestId: {}", 
                cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        
        validateFundingRequest(cmd.requestId());
        ProductResponse product = validateProductAvailability(cmd.productId(), cmd.quantity());
        Order order = createOrder(cmd, product);
        deductFunderWallet(cmd.funderId(), order.getTotalAmount());
        
        String orderId = repo.save(order).getId();
        log.info("Order placed successfully - OrderId: {}", orderId);
        
        syncToOrderView(order, product.name());
        updateProductInventory(cmd.productId(), product, cmd.quantity());
        
        sendOrderPlacedEvent(orderId, order, product);
        
        return orderId;
    }
    
    private void validateFundingRequest(String requestId) {
        log.info("Checking funding request status - RequestId: {}", requestId);
        FundingRequestResponse fundingRequest = investmentServiceClient.getFundingRequestById(requestId);
        
        if (fundingRequest == null) {
            log.error("Funding request not found - RequestId: {}", requestId);
            throw new RuntimeException("Funding request not found: " + requestId);
        }
        
        log.info("Funding request found - Status: {}, TargetAmount: {}, CurrentAmount: {}", 
                fundingRequest.status(), fundingRequest.targetAmount(), fundingRequest.currentAmount());
        
        if (!"FUNDED".equals(fundingRequest.status())) {
            log.error("Funding request is not fully funded - RequestId: {}, Status: {}", 
                    requestId, fundingRequest.status());
            throw new RuntimeException("Funding request is not fully funded. Status: " + fundingRequest.status());
        }
        
        log.info("Funding request validation passed - RequestId: {} is FUNDED", requestId);
    }
    
    private ProductResponse validateProductAvailability(String productId, int requestedQuantity) {
        log.info("Fetching product details - ProductId: {}", productId);
        ProductResponse product = productServiceClient.getProductById(productId);
        
        if (product == null) {
            log.error("Product not found - ProductId: {}", productId);
            throw new RuntimeException("Product not found: " + productId);
        }
        
        log.info("Product found - Name: {}, Price: {}, Available Quantity: {}", 
                product.name(), product.price(), product.quantity());
        
        if (product.quantity() < requestedQuantity) {
            log.error("Insufficient product quantity - ProductId: {}, Available: {}, Requested: {}", 
                    productId, product.quantity(), requestedQuantity);
            throw new RuntimeException("Insufficient product quantity. Available: " + product.quantity() + 
                    ", Requested: " + requestedQuantity);
        }
        
        log.info("Product availability validated");
        return product;
    }
    
    private Order createOrder(PlaceOrderRequest cmd, ProductResponse product) {
        log.info("Creating order entity");
        Order order = new Order(cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        order.setUnitPrice(product.price());
        order.setTotalAmount(order.getQuantity() * order.getUnitPrice());
        
        log.info("Order created - UnitPrice: {}, TotalAmount: {}", order.getUnitPrice(), order.getTotalAmount());
        return order;
    }
    
    private void deductFunderWallet(String funderId, double totalAmount) {
        BigDecimal walletAdjustment = BigDecimal.valueOf(-totalAmount);
        log.info("Deducting from funder wallet - FunderId: {}, Amount: {}", funderId, walletAdjustment);
        
        try {
            userServiceClient.updateUserWallet(funderId, walletAdjustment);
            log.info("Funder wallet updated successfully - FunderId: {}", funderId);
        } catch (RuntimeException e) {
            log.error("Failed to update funder wallet - FunderId: {}, Error: {}", funderId, e.getMessage());
            throw new RuntimeException("Failed to update funder wallet: " + e.getMessage(), e);
        }
    }
    
    private void updateProductInventory(String productId, ProductResponse product, int orderedQuantity) {
        int newQuantity = product.quantity() - orderedQuantity;
        log.info("Updating product inventory - ProductId: {}, Old Quantity: {}, New Quantity: {}", 
                productId, product.quantity(), newQuantity);
        
        productServiceClient.updateProductQuantity(productId, product, newQuantity);
        log.info("Product inventory updated successfully");
    }

    public String updateStatus(String id, String status) {
        log.info("Updating order status - OrderId: {}, NewStatus: {}", id, status);
        
        Order order = findOrderById(id);
        order.setStatus(status);
        
        if ("DELIVERED".equals(status)) {
            handleDeliveredStatus(order);
        }
        
        repo.save(order);
        log.info("Order status updated successfully - OrderId: {}, Status: {}", id, status);
        
        syncOrderViewStatus(id, status);
        return order.getStatus();
    }
    
    private Order findOrderById(String orderId) {
        Optional<Order> opt = repo.findById(orderId);
        if (opt.isEmpty()) {
            log.error("Order not found - OrderId: {}", orderId);
            throw new RuntimeException("Order not found: " + orderId);
        }
        return opt.get();
    }
    
    private void handleDeliveredStatus(Order order) {
        log.info("Order marked as DELIVERED - OrderId: {}", order.getId());
        order.setDeliveredAt(LocalDateTime.now());
        
        BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalAmount());
        transferFromAdminToSupplier(order.getSupplierId(), orderAmount);
    }
    
    private void transferFromAdminToSupplier(String supplierId, BigDecimal amount) {
        log.info("Transferring funds from ADMIN to Supplier - Amount: {}", amount);
        
        deductFromAdminWallet(amount);
        addToSupplierWallet(supplierId, amount);
    }
    
    private void deductFromAdminWallet(BigDecimal amount) {
        BigDecimal adminDeduction = amount.negate();
        log.info("Deducting from ADMIN wallet - AdminId: {}, Amount: {}", adminUserId, adminDeduction);
        
        try {
            userServiceClient.updateUserWallet(adminUserId, adminDeduction);
            log.info("ADMIN wallet updated successfully - AdminId: {}, Deducted: {}", adminUserId, amount);
        } catch (RuntimeException e) {
            log.error("Failed to deduct from ADMIN wallet - AdminId: {}, Error: {}", adminUserId, e.getMessage());
            throw new RuntimeException("Failed to deduct from ADMIN wallet: " + e.getMessage(), e);
        }
    }
    
    private void addToSupplierWallet(String supplierId, BigDecimal amount) {
        log.info("Adding to Supplier wallet - SupplierId: {}, Amount: {}", supplierId, amount);
        
        try {
            userServiceClient.updateUserWallet(supplierId, amount);
            log.info("Supplier wallet updated successfully - SupplierId: {}, Added: {}", supplierId, amount);
        } catch (RuntimeException e) {
            log.error("Failed to add to Supplier wallet - SupplierId: {}, Error: {}", supplierId, e.getMessage());
            rollbackAdminWalletDeduction(amount);
            throw new RuntimeException("Failed to add to Supplier wallet: " + e.getMessage(), e);
        }
    }
    
    private void rollbackAdminWalletDeduction(BigDecimal amount) {
        try {
            log.info("Rolling back ADMIN wallet - AdminId: {}", adminUserId);
            userServiceClient.updateUserWallet(adminUserId, amount);
            log.info("ADMIN wallet rollback successful - AdminId: {}", adminUserId);
        } catch (RuntimeException rollbackException) {
            log.error("Failed to rollback ADMIN wallet - AdminId: {}, Error: {}", 
                    adminUserId, rollbackException.getMessage());
        }
    }
    
    private void syncToOrderView(Order order, String productName) {
        log.info("Syncing order to order_views - OrderId: {}", order.getId());
        
        OrderView view = new OrderView();
        view.setOrderId(order.getId());
        view.setProductName(productName);
        view.setQuantity(order.getQuantity());
        view.setTotalAmount(order.getTotalAmount());
        view.setFunderName(order.getFunderId()); // Using ID as name for now
        view.setSupplierName(order.getSupplierId()); // Using ID as name for now
        view.setStatus(order.getStatus());
        view.setSupplierPaid(order.isSupplierPaid());
        
        orderViewRepository.save(view);
        log.info("Order synced to order_views successfully - OrderId: {}", order.getId());
    }
    
    private void syncOrderViewStatus(String orderId, String status) {
        log.info("Updating order_views status - OrderId: {}, Status: {}", orderId, status);
        
        OrderView view = orderViewRepository.findByOrderId(orderId);
        if (view != null) {
            view.setStatus(status);
            orderViewRepository.save(view);
            log.info("Order_views status updated successfully - OrderId: {}", orderId);
        } else {
            log.warn("OrderView not found for update - OrderId: {}", orderId);
        }
    }
    
    private void sendOrderPlacedEvent(String orderId, Order order, ProductResponse product) {
        log.info("Sending order placed event to Kafka - OrderId: {}", orderId);
        
        // Get user emails from user-service
        List<String> userIds = java.util.Arrays.asList(order.getFunderId(), order.getSupplierId());
        List<UserEmailResponse> users = userServiceClient.getUserEmailsByIds(userIds);
        
        if (users == null || users.isEmpty()) {
            log.error("Failed to retrieve user emails for notification - OrderId: {}", orderId);
            return;
        }
        
        // Find funder and supplier emails
        String funderEmail = null;
        String supplierEmail = null;
        
        for (UserEmailResponse user : users) {
            if (user.id().equals(order.getFunderId())) {
                funderEmail = user.email();
            } else if (user.id().equals(order.getSupplierId())) {
                supplierEmail = user.email();
            }
        }
        
        // Send notification to funder
        if (funderEmail != null) {
            sendNotificationMessage(orderId, funderEmail, 
                "Order Placed Successfully", 
                String.format("Your order #%s has been placed successfully. Product: %s, Quantity: %d, Total Amount: %.2f",
                    orderId, product.name(), order.getQuantity(), order.getTotalAmount()));
        }
        
        // Send notification to supplier
        if (supplierEmail != null) {
            sendNotificationMessage(orderId, supplierEmail, 
                "New Order Received", 
                String.format("You have received a new order #%s. Product: %s, Quantity: %d, Total Amount: %.2f",
                    orderId, product.name(), order.getQuantity(), order.getTotalAmount()));
        }
        
        log.info("Order notifications sent to Kafka - OrderId: {}", orderId);
    }
    
    private void sendNotificationMessage(String orderId, String email, String subject, String body) {
        java.util.Map<String, Object> message = new java.util.HashMap<>();
        message.put("key", orderId);
        message.put("email", email);
        message.put("subject", subject);
        message.put("body", body);
        message.put("orderId", orderId);
        message.put("timestamp", LocalDateTime.now().toString());
        
        ProducerRecord<String, Object> record = 
        new ProducerRecord<>("orderNotification", orderId, message);
        
        kafkaTemplate.send(record);
        log.info("Notification sent to Kafka - Email: {}, Subject: {}, Topic: orderNotification", email, subject);
    }
}
