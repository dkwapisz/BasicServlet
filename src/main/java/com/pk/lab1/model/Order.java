package com.pk.lab1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Entity(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Date orderDate;
    private Date deliveryDate;
    private String supplier;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private String additionalInformation;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderedProduct> orderedProducts;

    public Order(Date deliveryDate, String supplier, String customerEmail, String customerAddress, String customerPhone,
                 String additionalInformation, List<OrderedProduct> orderedProducts) {
        this.orderDate = new Date();
        this.deliveryDate = deliveryDate;
        this.supplier = supplier;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.additionalInformation = additionalInformation;
        this.orderedProducts = orderedProducts;
    }
}
