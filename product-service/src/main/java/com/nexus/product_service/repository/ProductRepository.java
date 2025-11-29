package com.nexus.product_service.repository;

import com.nexus.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
    Optional<Product> findByNameAndSupplierId(String name, String supplierId);
    List<Product> findByCategory(String category);
    List<Product> findBySupplierId(String supplierId);
    
}
