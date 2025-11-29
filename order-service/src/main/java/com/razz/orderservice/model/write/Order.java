package com.razz.orderservice.model.write;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "orders")
public class Order {
    @Id private String id;
    private String requestId;
    private String productId;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private String funderId;
    private String supplierId;
    private String status = "PLACED";
    private LocalDateTime placedAt = LocalDateTime.now();
    private LocalDateTime deliveredAt;
    private boolean supplierPaid = false;
    private LocalDateTime paidAt;

    public Order() {}

    public Order(String productId, int quantity, String funderId, String supplierId, String requestId) {
        this.productId = productId;
        this.quantity = quantity;
        this.funderId = funderId;
        this.supplierId = supplierId;
        this.requestId = requestId;
        this.totalAmount = 0.0; // Will be set by service
    }

    // GETTERS & SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRequestId() { return requestId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getFunderId() { return funderId; }
    public String getSupplierId() { return supplierId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPlacedAt() { return placedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public boolean isSupplierPaid() { return supplierPaid; }
    public void setSupplierPaid(boolean supplierPaid) { this.supplierPaid = supplierPaid; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}