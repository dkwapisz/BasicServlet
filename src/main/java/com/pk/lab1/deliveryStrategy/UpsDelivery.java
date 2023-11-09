package com.pk.lab1.deliveryStrategy;

import com.pk.lab1.model.Order;

public class UpsDelivery implements DeliveryStrategy {

    @Override
    public void deliver(Order order) {
        System.out.println("Order " + order.getOrderId() + " has been delivered by UPS");
    }

}

