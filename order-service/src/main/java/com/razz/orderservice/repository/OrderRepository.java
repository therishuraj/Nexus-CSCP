package com.razz.orderservice.repository;

import com.razz.orderservice.model.write.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> { }
