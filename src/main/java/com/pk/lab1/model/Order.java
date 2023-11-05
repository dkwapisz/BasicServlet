package com.pk.lab1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Date orderDate;
    private String supplier;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private String additionalInformation;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> orderedProducts;

    public Order(Date orderDate, String supplier, String customerEmail, String customerAddress, String customerPhone,
                 String additionalInformation, List<Product> orderedProducts) {
        this.orderDate = orderDate;
        this.supplier = supplier;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.additionalInformation = additionalInformation;
        this.orderedProducts = orderedProducts;
    }
}
