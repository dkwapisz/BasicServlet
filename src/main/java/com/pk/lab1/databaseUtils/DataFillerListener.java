package com.pk.lab1.databaseUtils;

import com.pk.lab1.model.Order;
import com.pk.lab1.model.Product;
import com.pk.lab1.repository.OrderRepository;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.Collections;
import java.util.Date;

@WebListener
public class DataFillerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        EntityManager entityManager = EntityManagerSingleton.getEntityManager();
        OrderRepository orderRepository = new OrderRepository(entityManager);
        ProductRepository productRepository = new ProductRepository(entityManager);

        Product product1 = new Product("Product 1", 10, 5);
        Product product2 = new Product("Product 2", 15, 8);
        Product product3 = new Product("Product 3", 20, 3);

        Order order = new Order(new Date(), "Supplier 1", "customer@example.com",
                "Address 123", "123-456-7890", "Additional Info", Collections.singletonList(product1));

        productRepository.addEntity(product1);
        productRepository.addEntity(product2);
        productRepository.addEntity(product3);

        orderRepository.addEntity(order);
    }

}