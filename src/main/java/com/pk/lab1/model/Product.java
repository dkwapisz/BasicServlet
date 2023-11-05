package com.pk.lab1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderedProduct> order;

    public Product(String name, int productPrice, int totalQuantity) {
        this.name = name;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.blockedQuantity = 0;
    }
}
