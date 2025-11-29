package com.nexus.product_service.service;

import com.nexus.product_service.model.Product;
import com.nexus.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
     
    public String CreateProduct(Product product, String supplierId) {
        // 1. Check if a product with the same name already exists
        Optional<Product> existingProduct = productRepository.findByNameAndSupplierId(
            product.getName(), 
            supplierId
        );

        if (existingProduct.isPresent()) {
            // 2. If it exists, return a specific error string (NO EXCEPTION THROWN)
            return String.format(
                " Product '%s' already exists for supplier ID '%s' please update it.",
                product.getName(),
                supplierId
            );
        }

        // 3. If it doesn't exist, proceed with creation
        product.setListedAt(java.time.LocalDateTime.now());
        product.setSupplierId(supplierId);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId(); // Return the ID of the newly created product (better practice)
    }

    public boolean updateProductQuantity(String id, int quantity) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setQuantity(quantity);
            productRepository.save(existingProduct);
            return true;
        }).orElse(false);
    }

    public List<Product> GetProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    public List<Product> GetProductsBySupplier(String supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }
    public List<Product> GetAllProducts() {
        return productRepository.findAll();
    }
    public List<Product> GetProductsInShortage() {
        return productRepository.findAll().stream()
                .filter(Product::isShortage)
                .toList();
    }
    public Optional<Product> getByID(String id) {
        return productRepository.findById(id);
    }
    
    public boolean updateProduct(String id, Product updatedProduct, String supplierId) {
        // Check if the product exists and if the supplierId matches
        boolean isSupplierValid = productRepository.findById(id)
            .map(existingProduct -> existingProduct.getSupplierId().equals(supplierId))
            .orElse(false);
    
        // If the supplierId is invalid, return false
        if (!isSupplierValid) {
            return false;
        }
    
        // Update the product if it exists
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setPrice(updatedProduct.getPrice());
            productRepository.save(existingProduct);
            return true;
        }).orElse(false);
    }
    
    public boolean deleteProduct(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
}
