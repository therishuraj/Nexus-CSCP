package com.nexus.product_service.controller;

import com.nexus.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

import com.nexus.product_service.model.Product;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/product")
    public Map<String,String> createProduct(@RequestBody Product product, @RequestHeader("X-User-Id") String supplierId) {

        log.info("Create product request received - Supplier ID: {}, Product: {}", supplierId, product);
            String productId = productService.CreateProduct(product, supplierId);
            log.info("Product created successfully - Product ID: {}", productId);
        return Map.of(
            "id", productId,
            "message", "Product created successfully"
        );
    }
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productService.GetAllProducts();
    }

    @GetMapping("/products/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        log.info("Fetching products by category: {}", category);
        return productService.GetProductsByCategory(category);
    }

    @GetMapping("/products/supplier/{supplierId}")
    public List<Product> getProductsBySupplier(@PathVariable String supplierId) {
        log.info("Fetching products for Supplier ID: {}", supplierId);
        return productService.GetProductsBySupplier(supplierId);
    }
    @GetMapping("/products/shortage")
    public List<Product> getProductsInShortage() {
        log.warn("Checking for products in shortage");
        return productService.GetProductsInShortage();
    }
    @PutMapping("/products/{id}/{quantity}")
    public Map<String, String> updateProductQuantity(@PathVariable String id, @PathVariable int quantity) {
        log.info("Updating product quantity - Product ID: {}, Quantity: {}", id, quantity);
        boolean updated = productService.updateProductQuantity(id, quantity);
        if (updated) {
            log.info("Product quantity updated successfully - Product ID: {}", id);
            return Map.of("message", "Product quantity updated successfully");
        } else {
            log.warn("Product not found while updating quantity - Product ID: {}", id);
            return Map.of("message", "Product not found");
        }
    }
    @GetMapping("/products/{id}")
    public Optional<Product> getProductById(@PathVariable String id) {
        log.info("Fetching product by ID: {}", id);
        return productService.getByID(id);
    }
    @PutMapping("/products/{id}")
    public Map<String, String> updateProduct(@PathVariable String id, @RequestBody Product updatedProduct, @RequestHeader("X-User-Id") String supplierId) {
        log.info("Updating product - Product ID: {}, Data: {}", id, updatedProduct);
        boolean updated = productService.updateProduct(id, updatedProduct,supplierId);
        if (updated) {
            log.info("Product updated successfully - Product ID: {}", id);
            return Map.of("message", "Product updated successfully");
        } else {
            log.warn("Product not found while updating - Product ID: {}", id);
            return Map.of("message", "Product not found");  
        }
    }
    @DeleteMapping("/products/{id}")
    public Map<String, String> deleteProduct(@PathVariable String id) {
        log.info("Deleting product - Product ID: {}", id);
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            log.info("Product deleted successfully - Product ID: {}", id);
            return Map.of("message", "Product deleted successfully");
        } else {
            log.warn("Product not found while deleting - Product ID: {}", id);
            return Map.of("message", "Product not found");
        }
    }
}
