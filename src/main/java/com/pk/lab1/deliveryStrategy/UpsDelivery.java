package com.pk.lab1.deliveryStrategy;

import com.pk.lab1.model.Order;

import static com.pk.lab1.utils.Utils.generateRandomString;

public class UpsDelivery implements DeliveryStrategy {

    @Override
    public void deliver(Order order) {
        System.out.println("Order " + order.getOrderId() + " has been delivered by UPS. " +
                "Tracking ID: " + generateRandomTrackingID());
    }

    @Override
    public String generateRandomTrackingID() {
        int length = 12;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return generateRandomString(length, characters);
    }
}

