package com.pk.lab1.repository;

import com.pk.lab1.model.Order;
import com.pk.lab1.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class OrderRepository extends BaseRepository<Order>{

    public OrderRepository(EntityManager entityManager) {
        super(entityManager, Order.class, "orders");
    }
}
