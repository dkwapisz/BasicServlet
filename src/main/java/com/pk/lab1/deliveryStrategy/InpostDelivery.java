package com.pk.lab1.deliveryStrategy;

import com.pk.lab1.model.Order;

import static com.pk.lab1.utils.Utils.generateRandomString;

public class InpostDelivery implements DeliveryStrategy {

    @Override
    public void deliver(Order order) {
        System.out.println("Order " + order.getOrderId() + " has been delivered by Inpost. " +
                "Tracking ID: " + generateRandomTrackingID());
    }

    @Override
    public String generateRandomTrackingID() {
        int length = 8;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return generateRandomString(length, characters);
    }
}
