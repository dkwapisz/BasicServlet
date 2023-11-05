package com.pk.lab1.repository;

import com.pk.lab1.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class OrderRepository {
    private final EntityManager entityManager;

    public OrderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Order> getAllOrders() {
        Query query = entityManager.createQuery("SELECT p FROM orders p");
        return query.getResultList();
    }

    public Order getOrderById(Long orderId) {
        return entityManager.find(Order.class, orderId);
    }

    public void addOrder(Order order) {
        entityManager.getTransaction().begin();
        entityManager.persist(order);
        entityManager.getTransaction().commit();
    }

    public void updateOrder(Order order) {
        entityManager.getTransaction().begin();
        entityManager.merge(order);
        entityManager.getTransaction().commit();
    }

    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);
        entityManager.getTransaction().begin();
        entityManager.remove(order);
        entityManager.getTransaction().commit();
    }
}
