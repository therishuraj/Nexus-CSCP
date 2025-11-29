package com.razz.orderservice.query.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.repository.OrderViewRepository;

@Service
public class OrderQueryService {
    private static final Logger log = LoggerFactory.getLogger(OrderQueryService.class);
    
    private final OrderViewRepository repo;
    
    public OrderQueryService(OrderViewRepository repo) { 
        this.repo = repo; 
    }

    public List<OrderView> getByUser(String userId) {
        log.debug("Querying orders by user - UserId: {}", userId);
        List<OrderView> orders = repo.findByFunderNameOrSupplierName(userId, userId);
        log.debug("Found {} orders for user - UserId: {}", orders.size(), userId);
        return orders;
    }
    
    public OrderView getById(String id) {
        log.debug("Querying order by ID - OrderId: {}", id);
        OrderView order = repo.findByOrderId(id);
        if (order != null) {
            log.debug("Found order - OrderId: {}, Status: {}", id, order.getStatus());
        } else {
            log.warn("Order not found - OrderId: {}", id);
        }
        return order;
    }
}