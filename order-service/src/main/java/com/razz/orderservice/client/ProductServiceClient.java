package com.razz.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.razz.orderservice.dto.ProductResponse;

@Component
public class ProductServiceClient {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);
    
    private final WebClient webClient;
    private final String productServiceUrl;

    public ProductServiceClient(
            WebClient webClient,
            @Value("${product.service.url:http://localhost:8081}") String productServiceUrl) {
        this.webClient = webClient;
        this.productServiceUrl = productServiceUrl;
    }

    public ProductResponse getProductById(String productId) {
        String url = productServiceUrl + "/api/v1/product/" + productId;
        log.info("Fetching product from product-service - ProductId: {}, URL: {}", productId, url);
        
        try {
            ProductResponse product = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();
            
            if (product != null) {
                log.info("Product fetched successfully - ProductId: {}, Name: {}, Price: {}, Quantity: {}", 
                        productId, product.name(), product.price(), product.quantity());
            } else {
                log.warn("Product not found - ProductId: {}", productId);
            }
            
            return product;
        } catch (WebClientResponseException e) {
            log.error("Failed to fetch product - ProductId: {}, Status: {}, Error: {}", 
                    productId, e.getStatusCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error calling product-service - ProductId: {}, Error: {}", productId, e.getMessage());
            throw e;
        }
    }

    public void updateProductQuantity(String productId, ProductResponse product, int newQuantity) {
        String url = productServiceUrl + "/api/v1/product/" + productId;
        log.info("Updating product quantity - ProductId: {}, OldQuantity: {}, NewQuantity: {}", 
                productId, product.quantity(), newQuantity);
        
        // Create updated product with new quantity
        ProductResponse updatedProduct = new ProductResponse(
                product.id(),
                product.name(),
                product.category(),
                newQuantity,
                product.price(),
                product.supplierId()
        );
        
        try {
            webClient.put()
                    .uri(url)
                    .bodyValue(updatedProduct)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            log.info("Product quantity updated successfully - ProductId: {}, NewQuantity: {}", productId, newQuantity);
        } catch (WebClientResponseException e) {
            log.error("Failed to update product quantity - ProductId: {}, Status: {}, Error: {}", 
                    productId, e.getStatusCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error calling product-service - ProductId: {}, Error: {}", productId, e.getMessage());
            throw e;
        }
    }
}
