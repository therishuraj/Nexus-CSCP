package com.razz.orderservice.repository;

import com.razz.orderservice.model.read.OrderView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface OrderViewRepository extends MongoRepository<OrderView, String> {
    @Query("{'$or': [{'funderName': ?0}, {'supplierName': ?0}]}")
    List<OrderView> findByFunderNameOrSupplierName(String funderName, String supplierName);
    OrderView findByOrderId(String orderId);
}