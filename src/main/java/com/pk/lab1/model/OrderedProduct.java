package com.pk.lab1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "orderedProducts")
@Data
@NoArgsConstructor
public class OrderedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private int quantity;

    public OrderedProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
