package com.pk.lab1.repository;

import com.pk.lab1.model.Order;
import jakarta.persistence.EntityManager;

public class OrderRepository extends BaseRepository<Order> {

    public OrderRepository(EntityManager entityManager) {
        super(entityManager, Order.class, "orders");
    }
}
