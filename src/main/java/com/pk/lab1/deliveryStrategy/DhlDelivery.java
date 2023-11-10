package com.pk.lab1.deliveryStrategy;

import com.pk.lab1.model.Order;

import static com.pk.lab1.utils.Utils.generateRandomString;

public class DhlDelivery implements DeliveryStrategy {

    @Override
    public void deliver(Order order) {
        System.out.println("Order " + order.getOrderId() + " will be delivered by DHL. " +
                "Tracking ID: " + generateRandomTrackingID());
    }

    @Override
    public String generateRandomTrackingID() {
        int length = 10;
        String characters = "0123456789";
        return generateRandomString(length, characters);
    }
}
