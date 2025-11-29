package com.razz.orderservice.model.read;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order_views")
public class OrderView {
    @Id private String orderId;
    private String productName;
    private int quantity;
    private double totalAmount;
    private String funderName;
    private String supplierName;
    private String status;
    private boolean supplierPaid;

    public OrderView() {}

    // GETTERS & SETTERS
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getFunderName() { return funderName; }
    public void setFunderName(String funderName) { this.funderName = funderName; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isSupplierPaid() { return supplierPaid; }
    public void setSupplierPaid(boolean supplierPaid) { this.supplierPaid = supplierPaid; }
}