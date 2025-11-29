package com.nexus.product_service.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String supplierId;
    private List<String> tags;
    private boolean isShortage;
    private int shortageThreshold;
    private LocalDateTime listedAt;
}
