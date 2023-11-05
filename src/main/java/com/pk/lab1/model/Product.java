package com.pk.lab1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String name;
    private int productPrice;
    private int totalQuantity;
    private int blockedQuantity;

    @ManyToOne
    private Order order;

    public Product(String name, int productPrice, int totalQuantity) {
        this.name = name;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.blockedQuantity = 0;
    }
}
