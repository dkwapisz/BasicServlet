package com.pk.lab1.model;

import com.pk.lab1.deliveryStrategy.DeliveryStrategy;
import com.pk.lab1.enums.DeliveryStatus;
import com.pk.lab1.enums.SupplierType;
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
    private Date deliveryDate;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private String additionalInformation;

    @Enumerated(EnumType.STRING)
    private SupplierType supplier;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Transient
    private DeliveryStrategy deliveryStrategy;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderedProduct> orderedProducts;

    public Order(Date deliveryDate, SupplierType supplier, String customerEmail, String customerAddress, String customerPhone,
                 String additionalInformation, List<OrderedProduct> orderedProducts) {
        this.orderDate = new Date();
        this.deliveryDate = deliveryDate;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.additionalInformation = additionalInformation;
        this.supplier = supplier;
        this.orderedProducts = orderedProducts;
        this.deliveryStatus = DeliveryStatus.ORDER_CREATED;
    }

    public Order(Date deliveryDate, SupplierType supplier, String customerEmail, String customerAddress, String customerPhone,
                 String additionalInformation, List<OrderedProduct> orderedProducts, DeliveryStatus deliveryStatus) {
        this.orderDate = new Date();
        this.deliveryDate = deliveryDate;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.additionalInformation = additionalInformation;
        this.supplier = supplier;
        this.orderedProducts = orderedProducts;
        this.deliveryStatus = deliveryStatus;
    }
}
