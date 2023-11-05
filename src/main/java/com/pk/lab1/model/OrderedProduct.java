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
    private Long orderedProductId;

    private int quantity;
    private int totalPrice;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    public OrderedProduct(int quantity, int totalPrice, Order order, Product product) {
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.order = order;
        this.product = product;
    }
}
