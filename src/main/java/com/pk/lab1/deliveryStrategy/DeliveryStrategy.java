package com.pk.lab1.deliveryStrategy;

import com.pk.lab1.model.Order;

public interface DeliveryStrategy {
    void deliver(Order order);
}
