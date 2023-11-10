package com.pk.lab1.deliveryStrategy;

import com.pk.lab1.model.Order;

import static com.pk.lab1.utils.Utils.generateRandomString;

public class PolishPostDelivery implements DeliveryStrategy {

    @Override
    public void deliver(Order order) {
        System.out.println("Order " + order.getOrderId() + " has been delivered by Polish Post. " +
                "Tracking ID: " + generateRandomTrackingID());
    }

    @Override
    public String generateRandomTrackingID() {
        int length = 8;
        String characters = "abcdefghijklmnopqrstuvwxyz";
        return generateRandomString(length, characters);
    }
}

